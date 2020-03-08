package com.example.petlover.ui.chatlog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petlover.R

class ChatlogAdapter(private val item: ArrayList<ChatlogModel>): RecyclerView.Adapter<ChatlogAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val sent = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_chatlogincome, parent, false)
        val recive = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_chatlogoutcome, parent, false)
        return ViewHolder(sent)
}



    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: ChatlogModel = item[position]
        holder.msg.text = model.msg
        holder.time.text = model.timestamp
    }
    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val  msg = v.findViewById(R.id.messagetext) as TextView
        val  time = v.findViewById(R.id.timecomingchat) as TextView

    }

}