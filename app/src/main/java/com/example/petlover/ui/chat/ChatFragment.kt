package com.example.petlover.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petlover.R
import com.example.petlover.databinding.FragmentNotificationsBinding
import com.example.petlover.ui.model.Model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class ChatFragment : Fragment() {

    private lateinit var notificationsViewModel: ChatViewModel
    private lateinit var database: DatabaseReference
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var chat = ArrayList<ChatModel>()
    private lateinit var binding: FragmentNotificationsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProviders.of(this).get(ChatViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications,container,false)
//        val textView: TextView = root.findViewById(R.id.text_notifications)
//        notificationsViewModel.text.observe(this, Observer {
//            textView.text = it
//        })  may be use
        binding.recyclerViewChat.layoutManager = GridLayoutManager(context,1, GridLayoutManager.VERTICAL, false)

        getIDListChat()
        return binding.root
    }
    private fun getIDListChat () {
        db.collection("chat")
            .whereArrayContains("uids","${auth.currentUser?.uid}")
            .get()
            .addOnSuccessListener {documents ->
                for (document in documents) {
                    Log.d("DataChat: ","${document.id} => ${document.data}")
                    chat.add(ChatModel("${document.data["uidsender"]}"
                        , "${document.data["uidreciver"]}", "${document.data["status"]}", "${document.id}"))
                }
                val adapter = ChatAdapter(chat)
                binding.recyclerViewChat.adapter = adapter
            }
            .addOnFailureListener {e ->
                Log.w("DataChat", "Error adding document", e)
            }
    }
}