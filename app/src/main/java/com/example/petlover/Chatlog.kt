package com.example.petlover

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
<<<<<<< HEAD
import com.google.firebase.database.*
=======
import com.example.petlover.ui.chatlog.ChatfromItem
import com.example.petlover.ui.chatlog.ChatlogModel
import com.example.petlover.ui.chatlog.ChattoItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_chatlog.*
import kotlinx.android.synthetic.main.layout_list_chatlogincome.view.*
import kotlinx.android.synthetic.main.layout_list_chatlogoutcome.view.*
>>>>>>> 5cb00b94bd4d6fb882f643775b72e96ab3bb97d2
import java.util.*

class Chatlog : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)
<<<<<<< HEAD
        var roomuid: String= "22Mk5iKKLdB4VmT8db2k"
        var uiduser: String = "svx67MwBcfO4rQ5dESBDSvXnrCH3"
        getchat(roomuid)
        sendmessage(uiduser,"Hello Dad",roomuid)
    }

    fun getchat(uid: String){
        var database = FirebaseDatabase.getInstance().getReference("/chat/${uid}")
=======
        var roomuid = "22Mk5iKKLdB4VmT8db2k"
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
>>>>>>> 5cb00b94bd4d6fb882f643775b72e96ab3bb97d2
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