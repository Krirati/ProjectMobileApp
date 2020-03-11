package com.example.petlover

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chatlog.*
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
    fun getchat(uid: String, roomuid: String){
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
        val setmssage = ChatlogModel(uiduser, msg, timeStamp)
        database.child("chat").child(roomuid).child("b").setValue(setmssage)
    }

    fun checkwhosend(uid:String,msg: String){
    }
}
