package com.example.petlover

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_chatlog.*
import kotlinx.android.synthetic.main.layout_list_chatlogincome.view.*
import kotlinx.android.synthetic.main.layout_list_chatlogoutcome.view.*
import java.util.*

class Chatlog : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    var useruid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)
        var roomuid = intent.getStringExtra("uidRoom").toString()
        getchat(useruid,roomuid)

        sendmessagebtn.setOnClickListener{

            Log.d("Send",useruid)
            Log.d("Send",edittextmessage.text.toString())
            if (useruid != null && edittextmessage.text != null) {
                sendmessage(useruid,edittextmessage.text.toString(),roomuid)
            }
            edittextmessage.text.clear()
            getchat(useruid,roomuid)
        }

    }

    fun getchat(uiduser: String,roomuid: String){
        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclechatlog.adapter = adapter
        db.collection("chat").document(roomuid).collection("chat").orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.data["fromuid"] != uiduser){
                        adapter.add(ChatfromItem(document.data["msg"].toString()))
                    }
                    else{
                        adapter.add(ChattoItem(document.data["msg"].toString()))
                    }
                    Log.d("getdata", "${document.id} => ${document.data["msg"]}")
                }
            }
    }

    fun sendmessage(uiduser: String,msg: String,roomuid: String){
        var database = FirebaseDatabase.getInstance()
        val timeStamp: String? = Calendar.getInstance().time.toString()
        val randuid = database.reference.push().key
        val word = hashMapOf(
            "fromuid" to uiduser,
            "msg" to msg,
            "timestamp" to timeStamp
        )
        Log.d("firebase",randuid)
        if (randuid != null) {
            db.collection("chat").document(roomuid).collection("chat").document(randuid).set(word)
        }
    }

}
class ChatfromItem(val text:String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.layout_list_chatlogincome
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messagetextincome.text = text
    }

}
class ChattoItem(val text: String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.layout_list_chatlogoutcome
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messagetextoutcome.text = text
    }

}
