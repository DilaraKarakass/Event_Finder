package com.example.myapplication

import Events
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.HomeActivity
import com.google.android.gms.maps.CameraUpdateFactory
//yeni eklenenler -----------
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest



//------------------------

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var eventList: List<Event> // HomeActivity'den gelen etkinlikler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        // Event listesi HomeActivity'den gönderilecek
        eventList = intent.getParcelableArrayListExtra("eventList")!!

        // Harita hazırlığı
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //yeni eklendi
        if (eventList.isEmpty()) {
            println("Event list is empty!")
        } else {
            println("Event list size: ${eventList.size}")
        }
        val builder = LatLngBounds.Builder()

        var firstEventLocation: LatLng? = null
        // Etkinlikleri haritaya pin olarak ekle
        for (event in eventList) {
            val eventLocation = LatLng(event.location.latitude, event.location.longitude)
            val marker=mMap.addMarker(MarkerOptions().position(eventLocation).title(event.name))
            //yoruma alındı alttaki
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 10f))
            //yeni eklendi
            // İlk etkinlik için kamerayı taşı
            if (marker != null) {
                Log.d("MapActivity", "Marker added for: ${event.name}")
            } else {
                Log.d("MapActivity", "Failed to add marker for: ${event.name}")
            }

            //_________________
            builder.include(eventLocation)
            marker?.tag = event
        }


        //_____________________
        // Harita sınırlarını oluşturduk
        val bounds = builder.build()

        // Haritayı bu sınırları kapsayacak şekilde ayarlayın
        val padding = 100 // Harita kenarlarında biraz boşluk bırakmak için
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)

        // Kamerayı yeni sınırlarla ayarlayın
        mMap.moveCamera(cameraUpdate)

        // Marker'a tıklama olayını ekleyelim
        mMap.setOnMarkerClickListener { marker ->
            val event = marker.tag as? Event
            event?.let {
                showEventDetails(it)
            }
            true // Tıklama olayını tüketiyoruz
        }

        // YENİ EKLENDİ
        // Harita kamerasını ilk etkinlik konumuna taşı
        //val var başında alttakiler yoruma alındı
//        firstEventLocation = LatLng(eventList[0].location.latitude, eventList[0].location.longitude)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstEventLocation, 10f))
        // Harita kamerayı ilk etkinlik konumuna taşı
        firstEventLocation?.let {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 10f))
        }

        // Buton referansı
        val backButton = findViewById<Button>(R.id.btnBack)

        // Butona tıklama olayı
        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Önceki aktiviteleri temizler
            startActivity(intent)
            finish() // MapActivity'yi kapatır
        }
    }

    // Etkinlik bilgilerini göstermek için AlertDialog
    private fun showEventDetails(event: Event) {
        val details = """
            Event Name: ${event.name}
            Category: ${event.category}
            Date: ${event.date}
            Description: ${event.description}
        """.trimIndent()

        val builder = AlertDialog.Builder(this)
        builder.setTitle(event.name) // Etkinlik adı başlık olarak
        builder.setMessage(details) // Etkinlik bilgileri içerik olarak

        // Yol tarifi butonunu ekliyoruz
        builder.setPositiveButton("Get Directions") { dialog, _ ->
            // Event'in lokasyonundan yol tarifi al
            val eventLocation = LatLng(event.location.latitude, event.location.longitude)
            openGoogleMapsForDirections(eventLocation)
            dialog.dismiss()
        }

        builder.setNegativeButton("OKEY") { dialog, _ ->
            dialog.dismiss()
        }


        val dialog = builder.create()
        dialog.show()

        //-----------------
    }
    private fun openGoogleMapsForDirections(destination: LatLng) {
        // Konum iznini kontrol et
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Kullanıcının mevcut konumunu almak için LocationManager kullanabiliriz
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            try {
                val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                if (location != null) {
                    val origin = LatLng(location.latitude, location.longitude) // Kullanıcının mevcut konumu

                    // Google Maps'ı yol tarifi için açma
                    val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}&travelmode=driving")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.google.android.apps.maps")
                    startActivity(intent)
                } else {
                    // Eğer kullanıcı konum bilgisi alamazsa, hata mesajı
                    Toast.makeText(this, "Konum bilgisi alınamıyor", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                // Konum izinleri yoksa, hata mesajı
                Toast.makeText(this, "Konum izni bulunmuyor. Lütfen izinleri kontrol edin.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Konum izni verilmemişse, izin iste
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }


}
