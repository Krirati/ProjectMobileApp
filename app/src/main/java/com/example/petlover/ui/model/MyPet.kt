package com.example.petlover.ui.model

import com.google.type.LatLng

data class MyPet (
    var name: String = "",
    var pedigree: String = "",
    var gender: String = "",
    var birthday: String = "",
    var location: String? = "",
    var contact: String?
)