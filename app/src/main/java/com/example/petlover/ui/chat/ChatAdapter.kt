package com.example.petlover.ui.chat

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.petlover.R
import androidx.recyclerview.widget.RecyclerView
import com.example.petlover.Chatlog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.layout_list_chat.view.*

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
        val uid = if (model.uidsender !== auth?.uid) model.uidsender else model.uidreciver
        db.collection("users")
            .document("$uid")
            .get()
            .addOnSuccessListener {
                Log.d("UserAdapeter", "${it.id} => ${it.data}")
                holder.username.text = it.data?.get("username").toString()
            }
        holder.message.text = model.uidsender
//        holder.time.text = model.time?.toDate().toString()
        when (model.status == "unread") {
            true -> holder.cardItem.setBackgroundResource(R.color.colorAccent)
        }
        holder.cardItem.setOnClickListener {
            val intent = Intent(it.context, Chatlog::class.java)
            intent.putExtra("uidRoom", "${model.idRoom}")
            it.context.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        init {itemView.cardchat.setOnClickListener {
//            val intent = Intent(itemView.context, Chatlog::class.java)
//            itemView.context.startActivity(intent)
//        }}
        val username = itemView.findViewById(R.id.username) as TextView
        val message = itemView.findViewById(R.id.message) as TextView
        val time = itemView.findViewById(R.id.time) as TextView
        val cardItem = itemView.findViewById(R.id.cardchat) as CardView

    }
}