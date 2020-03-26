package com.example.petlover.ui.model


import com.google.firebase.Timestamp

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