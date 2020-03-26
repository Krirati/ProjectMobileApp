package com.example.petlover.ui.model


import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Model(
    val name: String ="",
    val gender: String ="",
    val birthday: String ="",
    val imageUID: String? = null,
    val pedigree: String ="",
    val uid: String ="",
    val uidUser: String ="",
    val timestamp: Timestamp? = null,
    val imgRef: String?= ""
)