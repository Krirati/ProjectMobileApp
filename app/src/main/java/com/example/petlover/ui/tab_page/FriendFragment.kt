package com.example.petlover.ui.tab_page

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.petlover.R
import com.example.petlover.databinding.FragmentFriendBinding
import com.example.petlover.ui.home.HomeAdapter
import com.example.petlover.ui.home.HomeViewModel
import com.example.petlover.ui.model.Model
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_friend.*


class FriendFragment : Fragment() {
    var listItem = ArrayList<Model>()
    private lateinit var homeViewModel: HomeViewModel
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentFriendBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend,container,false)
        binding.apply {
            recyclerViewFriend.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
            swipeRefreshLayoutFriend.setOnRefreshListener {
                Handler().postDelayed({
                    listItem.clear()
                    logRecyclerView()
                    binding.swipeRefreshLayoutFriend.isRefreshing = false
                },3000)
            }
            swipeRefreshLayoutFriend.setColorSchemeColors(
                Color.parseColor("#008744")
                , Color.parseColor("#0057e7"), Color.parseColor("#d62d20"))
        }
        logRecyclerView()
        return binding.root
    }

    private fun logRecyclerView() {
        listItem.clear()
        db.collection("animals")
            .whereArrayContains("category", "Find friends")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("Data in animals", "${document.id} => ${document.data}")
                    val data = document.toObject(Model::class.java)
                    listItem.add(data)
                }
                val adapter = HomeAdapter(listItem)
                binding.recyclerViewFriend.adapter = adapter
                binding.progressBarFriend.visibility = View.INVISIBLE
            }
            .addOnFailureListener { exception ->
                Log.w("Data in animals", "Error getting documents.", exception)
            }
    }
}
