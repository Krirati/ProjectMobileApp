package com.example.petlover.ui.tab_page

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.petlover.R
import com.example.petlover.ui.home.HomeAdapter
import com.example.petlover.ui.home.HomeViewModel
import com.example.petlover.ui.home.Model
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.ArrayList


class CoupleFragment : Fragment() {
    var listItem = ArrayList<Model>()
    private lateinit var homeViewModel: HomeViewModel
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_couple, container, false)
        val recyclerView = root.findViewById(R.id.recyclerView) as RecyclerView
        val progressBarHome = root.findViewById(R.id.progressBarHome) as ProgressBar
        recyclerView.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        logRecyclerView(progressBarHome, recyclerView)
        return root
    }
    private fun logRecyclerView(progressBarHome: ProgressBar, recyclerView: RecyclerView) {
        db.collection("animals")
            .whereArrayContains("category", "Find a couple")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("Data in animals", "${document.id} => ${document.data}")
                    val data = document.toObject(Model::class.java)
                    listItem.add(data)
                }
                val adapter = HomeAdapter(listItem)
                recyclerView.adapter = adapter
                progressBarHome.visibility = View.INVISIBLE
            }
            .addOnFailureListener { exception ->
                Log.w("Data in animals", "Error getting documents.", exception)
            }
    }
}
