package com.example.petlover.ui.chat

import com.google.firebase.Timestamp

data class ChatModel(
//    val username: String? = "",
//    val time: Timestamp?,
//    val msg: String? = "",
//    val uids: ArrayList<String>?
    val uidsender: String?,
    val uidreciver: String?,
    val status: String?,
    val idRoom: String?,
    val timestamp: Timestamp?
    )