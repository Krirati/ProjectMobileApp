package com.example.petlover.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petlover.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class ChatFragment : Fragment() {

    private lateinit var notificationsViewModel: ChatViewModel
    private lateinit var database: DatabaseReference
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProviders.of(this).get(ChatViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
//        val textView: TextView = root.findViewById(R.id.text_notifications)
//        notificationsViewModel.text.observe(this, Observer {
//            textView.text = it
//        })  may be use
        val recyclerView = root.findViewById(R.id.recyclerViewChat) as RecyclerView
        database = FirebaseDatabase.getInstance().reference
        recyclerView.layoutManager = GridLayoutManager(context,1, GridLayoutManager.VERTICAL, false)
        val chat = ArrayList<ChatModel>()
        chat.add(ChatModel("dog","15:19", "Hello world"))
        chat.add(ChatModel("cat","15:19", "Hello world2"))
        chat.add(ChatModel("ant","15:19", "Hello world3"))
        chat.add(ChatModel("bird","15:19", "Hello world4"))
        chat.add(ChatModel("bird","15:19", "Hello world4"))
        val adapter = ChatAdapter(chat)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        return root
    }
    private fun getListChat () {
        db.collection("chat")
    }
}