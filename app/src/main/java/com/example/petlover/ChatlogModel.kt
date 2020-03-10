package com.example.petlover

data class ChatlogModel (
    var fromuid: String,
    var msg: String,
    var timestamp: String?
)
{
    constructor() : this("","","")
}