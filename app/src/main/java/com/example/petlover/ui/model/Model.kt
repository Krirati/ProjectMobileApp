package com.example.petlover.ui.model


import com.google.firebase.Timestamp

data class Model(
    val name: String ="",
    val gender: String ="",
    val birthday: String ="",
//    val imageUID: Uri? = null,
    val pedigree: String ="",
    val uid: String ="",
    val uidUser: String ="",
    val timestamp: Timestamp? = null,
    val category: ArrayList<String>? = null
)