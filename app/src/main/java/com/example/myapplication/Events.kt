import android.os.Parcel
import android.os.Parcelable
import com.example.myapplication.Event
import java.io.Serializable

//package com.example.myapplication
//import com.google.android.gms.maps.model.LatLng
//import java.io.Serializable
//
//data class Events(
//    val type: Int,
//    val category: String,
//    val name: String,
//    val date: String,
//    val location: LatLng
//): Serializable
//

data class Events(
    val eventlist: List<Event>
): Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }
}

