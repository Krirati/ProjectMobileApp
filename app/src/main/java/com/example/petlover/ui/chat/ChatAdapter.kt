package com.example.petlover.ui.chat

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.petlover.R
import androidx.recyclerview.widget.RecyclerView
import com.example.petlover.Chatlog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_list_chat.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//import com.example.petlover.Chat


class ChatAdapter (private val modelChatModel: ArrayList<ChatModel>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private var db = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_chat, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return modelChatModel.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: ChatModel = modelChatModel[position]
        val uid = if (model.uidsender == auth?.uid) model.uidreciver else model.uidsender
        Log.d("uidsss",uid)
        db.collection("users")
            .document("$uid")
            .get()
            .addOnSuccessListener {
                Log.d("UserAdapeter", "${it.id} => ${it.data}")
                holder.username.text = it.data?.get("username").toString()
            }
        holder.message.text = "มีข้อความ"
        val timestamp = model.timestamp?.toDate().toString().split('G')
        holder.time.text = timestamp[0]
        when (model.status == "unread") {
            true -> {
                holder.cardItem.setBackgroundResource(R.color.colorAccent)
                holder.message.text = "มีข้อความใหม่"
            }
            else -> {
                holder.message.text = "ไม่มีข้อความใหม่"
            }
        }
        Picasso.get()
            .load("${model.status}")
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.imgUser)
        holder.cardItem.setOnClickListener {
            db.collection("chat").document("${model.idRoom}").update("status", "read")
            val intent = Intent(it.context, Chatlog::class.java)
            intent.putExtra("uidRoom", "${model.idRoom}")
            intent.putExtra("reciveruid", uid)
            it.context.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username = itemView.findViewById(R.id.username) as TextView
        val message = itemView.findViewById(R.id.message) as TextView
        val time = itemView.findViewById(R.id.time) as TextView
        val cardItem = itemView.findViewById(R.id.cardchat) as CardView
        val imgUser = itemView.findViewById(R.id.userImg) as ImageView

    }
}