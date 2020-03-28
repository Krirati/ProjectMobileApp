package com.example.petlover.ui.chatlog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.petlover.ChatfromItem
import com.example.petlover.ChattoItem
import com.example.petlover.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_chatlog.*
import kotlinx.android.synthetic.main.layout_list_chatlogincome.view.*
import kotlinx.android.synthetic.main.layout_list_chatlogoutcome.view.*
import java.util.*

class Chatlog : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    var useruid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)
        var roomuid = "22Mk5iKKLdB4VmT8db2a"
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
        var database = FirebaseDatabase.getInstance().getReference("/chat/${roomuid}")
        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclechatlog.adapter = adapter
        database.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    val messa = it.getValue(ChatlogModel::class.java)
                    if (messa != null) {
                        if (messa.fromuid != uiduser){
                            adapter.add(ChatfromItem(messa.msg))
                        }
                        else{
                            adapter.add(ChattoItem(messa.msg))
                        }


                    }
                    Log.d("Message", it.toString())
                }
            }
        })

    }

    fun sendmessage(uiduser: String,msg: String,roomuid: String){
        var database = FirebaseDatabase.getInstance()
        val timeStamp: String? = Calendar.getInstance().time.toString()
        val randuid = database.reference.push().key
        Log.d("firebase",randuid)
        val setmssage =
            ChatlogModel(uiduser, msg, timeStamp)
        if (randuid != null) {
            database.reference.child("chat").child(roomuid).child(randuid).setValue(setmssage)
        }
        Log.d("roomId",roomuid)
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
