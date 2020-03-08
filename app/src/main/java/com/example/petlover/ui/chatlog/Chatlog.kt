package com.example.petlover.ui.chatlog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.petlover.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chatlog.*
import java.util.*
import kotlin.collections.ArrayList

class Chatlog : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)



        //val recyclechat = findViewById<re>()
        var adapter = ArrayList<ChatlogModel>()


        //var roomuid: String= "22Mk5iKKLdB4VmT8db2k"
        //var uiduser: String = "svx67MwBcfO4rQ5dESBDSvXnrCH3"
        //getchat(roomuid)
        //sendmessage(uiduser,"Hello Dad",roomuid)
    }

    fun getchat(uid: String){
        var database = FirebaseDatabase.getInstance().getReference("/chat/${uid}")
        database.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    val messa = it.getValue(ChatlogModel::class.java)
                    if (messa != null) {
                        Log.d("Message", messa.msg)
                    }
                    Log.d("Message", it.toString())
                }
            }
        })

    }

    fun sendmessage(uiduser: String,msg: String,roomuid: String){
        var database = FirebaseDatabase.getInstance().reference
        val timeStamp: String? = Calendar.getInstance().time.toString()
        val setmssage =
            ChatlogModel(uiduser, msg, timeStamp)
        database.child("chat").child(roomuid).child("b").setValue(setmssage)
    }

    fun checkwhosend(uid:String,msg: String){
    }
}
