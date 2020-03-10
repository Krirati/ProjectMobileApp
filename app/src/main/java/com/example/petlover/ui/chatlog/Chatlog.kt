package com.example.petlover.ui.chatlog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.petlover.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_chatlog.*
import java.util.*
import kotlin.collections.ArrayList

class Chatlog : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)
        var roomuid: String= "22Mk5iKKLdB4VmT8db2k"
        var uiduser: String = "svx67MwBcfO4rQ5dESBDSvXnrCH3"


        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclechatlog.adapter = adapter

        adapter.add(ChatfromItem())
        adapter.add(ChatfromItem())
        adapter.add(ChatfromItem())
        adapter.add(ChattoItem())
        adapter.add(ChattoItem())
        adapter.add(ChattoItem())



        sendmessagebtn.setOnClickListener{
            var useruid = FirebaseAuth.getInstance().currentUser?.uid
            if (useruid != null) {
                sendmessage(useruid,edittextmessage.text.toString(),roomuid)
            }
            getchat(roomuid)
        }


        sendmessage(uiduser,"Hello Dad",roomuid)
    }

    fun getchat(roomuid: String){
        var database = FirebaseDatabase.getInstance().getReference("/chat/${roomuid}")
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

}
class ChatfromItem: Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.layout_list_chatlogincome
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

}
class ChattoItem: Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.layout_list_chatlogoutcome
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

}
