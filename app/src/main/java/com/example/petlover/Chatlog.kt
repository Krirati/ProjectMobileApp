package com.example.petlover

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FieldValue
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
        var roomuid = intent.getStringExtra("uidRoom")
        var reciveruid = intent.getStringExtra("reciveruid")
        db.collection("users").document(reciveruid).get().addOnSuccessListener {
            title = it["username"].toString()
        }
        getchat(useruid,roomuid)
        db.collection("chat").document(roomuid).collection("chat").addSnapshotListener{
                snapshot, _ ->
            if (snapshot != null && snapshot.size() != 0) {
                getchat(useruid,roomuid)
            }
        }

        sendmessagebtn.setOnClickListener{

            Log.d("Send",useruid)
            Log.d("Send",edittextmessage.text.toString())
            if (edittextmessage.text.isNotBlank()) {
                sendmessage(useruid,edittextmessage.text.toString(),roomuid)
            }
            edittextmessage.text.clear()
        }

    }

    fun getchat(uiduser: String,roomuid: String){
        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclechatlog.adapter = adapter
        db.collection("chat").document(roomuid).collection("chat").orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var msg = document.data["msg"].toString()
                    var timestamp = document.data["timestamp"].toString()
                    if(document.data["fromuid"] != uiduser){
                        Log.d("timestamp",timestamp)
                        adapter.add(ChatfromItem(msg,timestamp))
                        recyclechatlog.scrollToPosition(adapter.itemCount-1)
                    }
                    else{
                        adapter.add(ChattoItem(msg,timestamp))
                        recyclechatlog.scrollToPosition(adapter.itemCount-1)
                    }
                    Log.d("getdata", "${document.id} => ${document.data["msg"]}")
                }
            }
    }

    fun sendmessage(uiduser: String,msg: String,roomuid: String){
        var database = FirebaseDatabase.getInstance()
        val randuid = database.reference.push().key
        val word = hashMapOf(
            "fromuid" to uiduser,
            "msg" to msg,
            "timestamp" to FieldValue.serverTimestamp()
        )
        val status = hashMapOf(
            "timestamp" to FieldValue.serverTimestamp(),
            "status" to "unread"
        )
        Log.d("firebase",randuid)
        if (randuid != null) {
            db.collection("chat").document(roomuid).collection("chat").document(randuid).set(word)
            db.collection("chat").document(roomuid).update(status)
        }
    }

}
class ChatfromItem(val text:String, val time:String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.layout_list_chatlogincome
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messagetextincome.text = text
        //viewHolder.itemView.timeincome.text = time
    }

}
class ChattoItem(val text: String, val time: String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.layout_list_chatlogoutcome
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messagetextoutcome.text = text
        //viewHolder.itemView.timeoutcome.text = time
    }

}
