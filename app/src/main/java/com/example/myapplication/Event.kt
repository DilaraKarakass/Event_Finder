package com.example.myapplication

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint


//YENİ EKLENDİ----------
import android.os.Parcel
import android.os.Parcelable
//------------------
data class Event(
    val name: String,
    val description: String,
    val category: String,
    val date: String,
    val location: LatLng,  // location: LatLng
    var isFavorite: Boolean = false // isFavorite: Boolean
    //val url: String,
    //YENİ EKLENDİİ-----
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "", // 'name' parametresi
        parcel.readString() ?: "", // 'description' parametresi
        parcel.readString() ?: "", // 'category' parametresi
        parcel.readString() ?: "", // 'date' parametresi

       parcel.readParcelable(LatLng::class.java.classLoader) ?: LatLng(0.0, 0.0), // 'location' parametresi


        parcel.readByte() != 0.toByte() // 'isFavorite' boolean değeri (0 => false, 1 => true)
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(category)
        parcel.writeString(date)
        parcel.writeParcelable(location, flags)
        parcel.writeByte(if (isFavorite) 1 else 0) // 'isFavorite' boolean değeri
    }


    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }
}
//----------------------------



data class EventResponse(
    val _embedded: EmbeddedEvents
)

// Embedded Events contains the list of events
data class EmbeddedEvents(
    val events: List<Event>
)

//yeni eklendii-------------

data class ParcelableLatLng(
    val latitude: Double,
    val longitude: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ParcelableLatLng> {
        override fun createFromParcel(parcel: Parcel): ParcelableLatLng {
            return ParcelableLatLng(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableLatLng?> {
            return arrayOfNulls(size)
        }
    }
}
// -------------------------

