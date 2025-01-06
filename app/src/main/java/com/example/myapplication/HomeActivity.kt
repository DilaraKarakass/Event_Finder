package com.example.myapplication
import EventAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


//---------------
class HomeActivity : AppCompatActivity() {

    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventList: List<Event>
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var searchView: SearchView
    // yeni eklendi----
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val TAG = "HomeActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        // Bildirim izni kontrolü
        //yeni ekledim pazar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
        //yeni ekledim pazar
        // Bildirim kanalı oluştur
        createNotificationChannel(this)

        // RecyclerView, Spinner bağlantıları
        recyclerView = findViewById(R.id.eventList)
        spinner = findViewById(R.id.categorySpinner)
        searchView = findViewById(R.id.searchView)
        eventList = mutableListOf()

        // RecyclerView ayarları
        eventAdapter = EventAdapter(this,eventList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = eventAdapter

        // Spinner için kategori seçenekleri
        val categories = listOf("All", "Sports","Art", "Technology", "Food", "Cinema")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // Ticketmaster API'den etkinlikleri al
        fetchEvents()
        //eventList.sortedBy { it.location }
        Log.d(TAG,"eventList size ${eventList.size}")
        // Spinner ile kategori filtreleme
        setupSpinner(categories)
        setupSearchView()

        // Menü butonuna tıklama işlemi için popup menü
        val btnMenu: ImageButton = findViewById(R.id.btnMenu)
        btnMenu.setOnClickListener { showPopupMenu(it) }

        //YENİ EKLENDİ----------
        // FusedLocationProviderClient'ı başlat
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Konum izni kontrolü yap
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Konumu al
            getLocation()
        } else {
            // İzin yoksa izin iste
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        //-----------------
    }

    // Fetched etkinliklere göre bildirim gönderme
    private fun sendEventNotification(event: Event) {
        // Bildirimin başlığını ve içeriğini oluştur
        val title = "New Event!"
        val content = "Event: ${event.name} ${event.date}"

        // NotificationHelper fonksiyonu çağırarak bildirim oluştur
        NotificationHelper(this@HomeActivity).createNotification(title, content)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchEvents(){
        //LKoK5bvPqKErkzKMToQQtkyHX2st7zgQ
        val apiKey = "LKoK5bvPqKErkzKMToQQtkyHX2st7zgQ"
        var list: List<Event> = emptyList()
        val link = "https://app.ticketmaster.com/discovery/v2/events.json?size=15&apikey=$apiKey"

        lifecycleScope.launch(Dispatchers.IO) {
            val url = URL(link)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.setRequestProperty("Content-Type", "application/json")
            httpURLConnection.setRequestProperty("Accept", "application/json")

            val inputStreamReader = InputStreamReader(httpURLConnection.inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val response = StringBuilder()

            bufferedReader.forEachLine {
                response.append(it)
            }
            bufferedReader.close()
//            Log.d("HomeActivity", "Response size : ${response.toString().length}")
//            Log.d("HomeActivity", response.toString())
            eventList = ticketmasterResponse(response.toString())
            runOnUiThread {
                //YENİ EKLEDİM PAZAR---------
                // En yakın etkinlikleri bulduktan sonra bildirim gönder
                eventList.firstOrNull()?.let { event ->
                    sendEventNotification(event) // Bildirim gönderme
                }
                //----------------
                //YENİLİK YAPIYORUM AŞAĞIDAKİ ASIL KOD YORUMA ALDIM
                // eventAdapter.updateList(eventList)
                getLocation()//BU YENİİ
            }
            Log.d(TAG,"list size ${list.size}")

        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = eventList.filter {
                    // Null olmayan bir değerle karşılaştırmak için name'i kontrol ediyoruz
                    it.name.contains(newText.orEmpty(), ignoreCase = true) == true
                }
                eventAdapter.updateList(filteredList)
                return true
            }
        })
    }
    private fun setupSpinner(categories: List<String>) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                filterEventsByCategory(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this@HomeActivity, view)
        menuInflater.inflate(R.menu.menu_options, popupMenu.menu) // Menü XML dosyasını ekleyin
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_map -> {
                    // Profile sayfasına git
                    val intent = Intent(this@HomeActivity, MapActivity::class.java)
                    intent.putParcelableArrayListExtra("eventList", ArrayList(eventList))
                    startActivity(intent)
                    true
                }

                R.id.action_profile -> {
                    // Map sayfasına git
                    val intent = Intent(this@HomeActivity, ProfileSettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
    //YENİ EKLENDİ
    // Konum izni yanıtını işleme
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted!", Toast.LENGTH_SHORT).show()
                getLocation()
            } else {
                Toast.makeText(this, "Location permission not granted!", Toast.LENGTH_SHORT).show()
            }
        }
        // Bildirim izni yanıtını işleme
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Bildirim izni verildi

                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission not granted!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ticketmasterResponse(response: String): List<Event> {
        return try {
            val jsonResponse = JSONObject(response)
            val eventsArray = jsonResponse.getJSONObject("_embedded").getJSONArray("events")
            val eventList = mutableListOf<Event>()
            for (i in 0 until eventsArray.length()) {
                val eventJson = eventsArray.getJSONObject(i)
                val promoterDescription = if (eventJson.has("promoter")) {
                    eventJson.getJSONObject("promoter").getString("description")
                } else {
                    "No description"
                }

                // Create an Event object from Ticketmaster response
                val event = Event(
                    name = eventJson.getString("name"),
                    description = promoterDescription,
                    date = "${
                        eventJson
                            .getJSONObject("dates")
                            .getJSONObject("start")
                            .getString("localDate")
                    } ${
                        eventJson.getJSONObject("dates").getJSONObject("start")
                            .getString("localTime")
                    }",
                    category = eventJson
                        .getJSONObject("_embedded")
                        .getJSONArray("attractions")
                        .getJSONObject(0)
                        .getJSONArray("classifications")
                        .getJSONObject(0)
                        .getJSONObject("segment")
                        .getString("name"),
                    location = LatLng(
                        eventJson
                            .getJSONObject("_embedded")
                            .getJSONArray("venues")
                            .getJSONObject(0)
                            .getJSONObject("location")
                            .getDouble("latitude"),
                        eventJson
                            .getJSONObject("_embedded")
                            .getJSONArray("venues")
                            .getJSONObject(0)
                            .getJSONObject("location")
                            .getDouble("longitude")
                    )
                )
                eventList.add(event)
            }
            eventList
        } catch (e: Exception) {
            Log.d(TAG,e.toString())
            emptyList()
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Konum başarılı bir şekilde alındıysa
                location?.let {
                    // Kullanıcının mevcut konumunu aldıktan sonra, etkinlikleri filtreleyebiliriz
                    filterEventsByLocation(it.latitude, it.longitude)
                }
            }
    }

    // Haversine formülüyle iki nokta arasındaki mesafeyi hesaplar
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Dünya'nın yarıçapı (km cinsinden)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c // Mesafe kilometre cinsindendir
    }
    private fun filterEventsByLocation(userLat: Double, userLon: Double) {
        val maxDistance = 1000.0// Max mesafe (km cinsinden)

        val nearbyEvents = eventList.filter { event ->
            val eventLat = event.location.latitude
            val eventLon = event.location.longitude
            val distance = calculateDistance(userLat, userLon, eventLat, eventLon)

            distance <= maxDistance // Eğer mesafe belirlediğiniz maxDistance'ın altında ise
        }

        // Filtrelenmiş etkinlik listesini RecyclerView'e aktarın
        eventAdapter.updateList(nearbyEvents)
        // Filtrelenmiş etkinlikleri MapActivity'ye gönder
//        val intent = Intent(this, MapActivity::class.java)
//        intent.putParcelableArrayListExtra("eventList", ArrayList(nearbyEvents)) // Filtrelenmiş etkinlikleri gönderiyoruz
//        startActivity(intent)
    }

    //YENİ EKLENDİ
    private fun filterEventsByCategory(selectedCategory: String) {
        val filteredList = if (selectedCategory == "All") {
            eventList // Eğer "All" kategorisi seçilmişse, tüm etkinlikleri göster
        } else {
            eventList.filter { event -> event.category.equals(selectedCategory, ignoreCase = true) }
        }

        // Filtrelenmiş etkinlik listesini RecyclerView'e aktarın
        eventAdapter.updateList(filteredList)
    }
    
    //yeni ekledim pazar
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "event_channel", // Kanalın ID'si
                "Etkinlik Bildirimleri", // Kanal adı
                NotificationManager.IMPORTANCE_DEFAULT // Kanalın önemi
            )
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

