package com.example.myapplication
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EventDetailActivity : AppCompatActivity() {
    private val CHANNEL_ID = "event_notifications"

    private lateinit var event: Event
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_event_detail)


        //pazartesi eklendi
        // Notification Channel Oluştur
        createNotificationChannel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnBack3 = findViewById<Button>(R.id.btnBack3)
        btnBack3.setOnClickListener {
            finish()
        }
        event = intent.getParcelableExtra("event")!!
//
//        // Etkinlik bilgilerini UI'ya yerleştirme
        val nameTextView: TextView = findViewById(R.id.eventName)
        val categoryTextView: TextView = findViewById(R.id.eventCategory)
        val dateTextView: TextView = findViewById(R.id.eventDate)
        val descriptionTextView: TextView = findViewById(R.id.eventDescription)
        val locationTextView: TextView = findViewById(R.id.eventLocation)
        val favoriteButton: ImageButton = findViewById(R.id.favoriteButton)
//
        categoryTextView.text = event.category
        nameTextView.text = event.name
        dateTextView.text = event.date
        descriptionTextView.text = event.description
        locationTextView.text = "Location: ${event.location.latitude}, Long: ${event.location.longitude}"
        if (event.isFavorite) {
            favoriteButton.setImageResource(R.drawable.fav_fill_icon)

        } else {
            favoriteButton.setImageResource(R.drawable.fav_icon)
        }

        favoriteButton.setOnClickListener {
            event.isFavorite = !event.isFavorite  // Favori durumunu değiştir
            if (event.isFavorite) {
                favoriteButton.setImageResource(R.drawable.fav_fill_icon)  // İçerisi dolu kalp
            } else {
                favoriteButton.setImageResource(R.drawable.fav_icon)  // İçerisi boş kalp
            }
            Toast.makeText(this,"Event added to favorites!", Toast.LENGTH_LONG).show()

            // Burada, favori durumu değiştikten sonra gerekli işlemleri yapabilirsiniz (örneğin, veri kaydetme)
            // Eğer veri kalıcı olmalıysa SharedPreferences veya veritabanı kullanabilirsiniz

        }
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Event Notifications"
            val descriptionText = "Notifications for event updates"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Kanalı NotificationManager'a kaydet
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

