package com.example.petlover

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chatlog.*
import kotlinx.android.synthetic.main.layout_datetime.view.*
import kotlinx.android.synthetic.main.layout_list_chatlogincome.view.*
import kotlinx.android.synthetic.main.layout_list_chatlogoutcome.view.*
import kotlinx.coroutines.Delay
import java.text.SimpleDateFormat
import java.util.*


class Chatlog : AppCompatActivity() {
    var database = FirebaseDatabase.getInstance()
    var beforedate = "adad"
    var firsttime = 0
    //val adapter = GroupAdapter<GroupieViewHolder>()
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
                snapshot, e ->
            if (e != null) {

                Log.w("errorsnap", "listen:error", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.metadata.hasPendingWrites()){
                db.waitForPendingWrites().addOnSuccessListener {
                    getchat(useruid,roomuid)
                }
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

    private fun getchat(uiduser: String, roomuid: String){
        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclechatlog.adapter = adapter
        val sdf = SimpleDateFormat("HH:mm")
        val daymode = SimpleDateFormat("dd  MMMM  yyyy")
        var msg: String
        var jo = 0
        if (firsttime == 0){
            Log.d("hello","hello diff")
            db.collection("chat").document(roomuid).collection("chat").orderBy("timestamp")
                .get()
                .addOnCompleteListener {
            it.addOnSuccessListener{ result ->
                    for (document in result) {
                        msg = document.data["msg"].toString()
                        Log.d("Hello",msg)
                        var timestamp = document.data["timestamp"] as Timestamp
                        Log.d("Hello",timestamp.toString())
                        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                        val netDate = Date(milliseconds)
                        val timer = sdf.format(netDate).toString()
                        val date = daymode.format(netDate).toString()
                        if(beforedate != date){
                            adapter.add(Createdate(date))
                        }
                        beforedate = if(document.data["fromuid"] != uiduser){
                            adapter.add(ChatfromItem(msg,timer))
                            recyclechatlog.scrollToPosition(adapter.itemCount-1)
                            date
                        } else{
                            adapter.add(ChattoItem(msg,timer))
                            recyclechatlog.scrollToPosition(adapter.itemCount-1)
                            date
                        }
                    }
                }
        }
            //firsttime++
            }
        /*else{
            Log.d("hello","hello johny")
            db.collection("chat").document(roomuid).collection("chat").orderBy("timestamp")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        jo++
                        if(jo == result.size()){
                            msg = document.data["msg"] as String
                            Log.d("lastword",msg)
                            var timestamp = document.data["timestamp"] as Timestamp
                            Log.d("Hello",timestamp.toString())
                            val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                            val netDate = Date(milliseconds)
                            val timer = sdf.format(netDate).toString()
                            val date = daymode.format(netDate).toString()
                            if(beforedate != date){
                                adapter.add(Createdate(date))
                            }

                            beforedate = if(document.data["fromuid"] != uiduser){
                                adapter.add(ChatfromItem(msg,timer))
                                recyclechatlog.scrollToPosition(adapter.itemCount-1)
                                date
                            } else{
                                adapter.add(ChattoItem(msg,timer))
                                recyclechatlog.scrollToPosition(adapter.itemCount-1)
                                date
                            }

                        }

                    }

                }
        }*/
    }

    private fun sendmessage(uiduser: String, msg: String, roomuid: String){
        val timestamp = FieldValue.serverTimestamp()
        val randuid = database.reference.push().key
        val word = hashMapOf(
            "timestamp" to timestamp,
            "fromuid" to uiduser,
            "msg" to msg

        )
        val status = hashMapOf(
            "timestamp" to timestamp,
            "status" to "unread"
        )
        Log.d("firebase",randuid)

        if (randuid != null) {
            db.collection("chat").document(roomuid).collection("chat").document(randuid).set(word)
            db.collection("chat").document(roomuid).collection("chat").document(randuid).update(word)
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
        viewHolder.itemView.timeincome.text = time
    }

}
class ChattoItem(val text: String, val time: String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.layout_list_chatlogoutcome
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messagetextoutcome.text = text
        viewHolder.itemView.timeoutcome.text = time
    }

}

class Createdate(val text: String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.layout_datetime
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.showdatechat.text = text
    }

}
