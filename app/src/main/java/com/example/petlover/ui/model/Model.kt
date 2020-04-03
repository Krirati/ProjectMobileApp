package com.example.petlover.ui.model


import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Model(
    val name: String ="",
    val gender: String ="",
    val birthday: String ="",
    val imageUID: String? = null,
    val pedigree: String = "",
    val contact: String? = "",
    val uid: String ="",
    val uidUser: String ="",
    val timestamp: Timestamp? = null,
    val imgRef: String?= ""
)
