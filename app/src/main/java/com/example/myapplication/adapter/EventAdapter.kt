import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.Event
import com.example.myapplication.EventDetailActivity


class EventAdapter(private val context:Context,private var events: List<Event>) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position] // eventList yerine events kullanıyoruz
        holder.eventName.text = event.name // 'Event' yerine 'event' küçük harf ile doğru nesneyi kullanıyoruz
        holder.eventDate.text = event.date
        holder.eventCategory.text = event.category
        //YENİ EKLENDİ
        // Konumu TextView'da göstermek için
        holder.eventLocation.text = "Location: ${event.location.latitude}, ${event.location.longitude}"

        // Favori durumunu kontrol et ve simgeyi ona göre ayarla
        if (event.isFavorite) {
            holder.favoriteButton.setImageResource(R.drawable.fav_fill_icon)  // İçerisi dolu kalp
        } else {
            holder.favoriteButton.setImageResource(R.drawable.fav_icon)  // İçerisi boş kalp
        }
        // Tıklama işlemi
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EventDetailActivity::class.java)
            intent.putExtra("event",event)
//            intent.putExtra("eventName", event.name)
//            intent.putExtra("eventDate", event.date)
//            intent.putExtra("eventCategory", event.category)
//            intent.putExtra("eventDescription", event.description)
//            intent.putExtra(
//                "eventLocation",
//                "Location: ${event.location.latitude}, Lng: ${event.location.longitude}"
//            )
            context.startActivity(intent)
        }
        // Favori butonuna tıklama işlemi
        holder.favoriteButton.setOnClickListener {
            event.isFavorite = !event.isFavorite  // Favori durumunu değiştir
            if (event.isFavorite) {
                holder.favoriteButton.setImageResource(R.drawable.fav_fill_icon)  // İçerisi dolu kalp
            } else {
                holder.favoriteButton.setImageResource(R.drawable.fav_icon)  // İçerisi boş kalp
            }
            Toast.makeText(context,"Event added to favorites!",Toast.LENGTH_LONG).show()

            // Burada, favori durumu değiştikten sonra gerekli işlemleri yapabilirsiniz (örneğin, veri kaydetme)
            // Eğer veri kalıcı olmalıysa SharedPreferences veya veritabanı kullanabilirsiniz

        }
        holder.calendarButton.setOnClickListener {
            val dateParts = event.date.split("-") // Assuming date format is "yyyy-MM-dd HH:mm"
            val dateTimeParts = dateParts[2].split(" ") // Separate day and time
            val timeParts = dateTimeParts[1].split(":") // Split hours and minutes

            val year = dateParts[0].toInt()
            val month = dateParts[1].toInt() - 1 // Month is 0-based in Calendar
            val day = dateTimeParts[0].toInt()
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()

            val startMillis = java.util.Calendar.getInstance().apply {
                set(year, month, day, hour, minute)
            }.timeInMillis

            val calendarIntent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, event.name)
                putExtra(CalendarContract.Events.DESCRIPTION, event.description)
                putExtra(CalendarContract.Events.EVENT_LOCATION, "${event.location.latitude}, ${event.location.longitude}")
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                putExtra(CalendarContract.Events.CALENDAR_DISPLAY_NAME, event.category) // Optional
            }

            // Check if there is an activity that can handle this intent
            try {
                context.startActivity(calendarIntent)
            } catch (e: Exception) {
                // Log the error for debugging
                Log.e("EventAdapter", "Failed to start calendar intent", e)
            }
        }
    }

    override fun getItemCount(): Int {
        return events.size // 'eventList' yerine 'events' kullanıyoruz
    }
    fun updateList(newList: List<Event>) {
//        events.clear() // yeni listeyi eklemeden önce temizliyoruz
//        events.addAll(newList)
        this.events = newList
        notifyDataSetChanged()
    }
    // ViewHolder ismini EventViewHolder'dan ViewHolder olarak güncelledik
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.eventName)
        val eventDate: TextView = itemView.findViewById(R.id.eventDate)
        val eventCategory: TextView = itemView.findViewById(R.id.eventCategory)
        //SONRADAN EKLENDİ
        val favoriteButton: ImageView = itemView.findViewById(R.id.favoriteButton)
        val calendarButton: ImageView = itemView.findViewById(R.id.calendarButton)
        val eventLocation:TextView= itemView.findViewById(R.id.eventLocation)

    }
}
