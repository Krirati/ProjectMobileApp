package com.example.petlover.ui.model

data class ChatlogModel (
    var fromuid: String,
    var msg: String,
    var timestamp: String?
)
{
    constructor() : this("","","")
}