package com.example.petlover.ui.tab_page

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.Filter
import android.widget.SearchView
import androidx.core.view.OneShotPreDrawListener.add
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager

import com.example.petlover.R
import com.example.petlover.databinding.FragmentCoupleBinding
import com.example.petlover.ui.home.HomeAdapter
import com.example.petlover.ui.home.HomeViewModel
import com.example.petlover.ui.model.Model
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.activity_addpet.*
import org.w3c.dom.Text



class CoupleFragment : Fragment() {
    var listItem = ArrayList<Model>()
    lateinit var list: MutableList<Model>
    private lateinit var homeViewModel: HomeViewModel
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentCoupleBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_couple,container,false)
        logRecyclerView()
        binding.apply {
            recyclerView.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
            swipeRefreshLayout.setOnRefreshListener {
                Handler().postDelayed({
                    listItem.clear()
                    logRecyclerView()
                    binding.swipeRefreshLayout.isRefreshing = false
                },3000)
            }
            swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#008744")
                ,Color.parseColor("#0057e7"),Color.parseColor("#d62d20"))
        }
        setHasOptionsMenu(true)
        return binding.root
    }
    private fun logRecyclerView() {
        listItem.clear()
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
                binding.recyclerView.adapter = adapter
                binding.progressBarHome.visibility = View.INVISIBLE
                
            }
            .addOnFailureListener { exception ->
                Log.w("Data in animals", "Error getting documents.", exception)
            }
    }

    fun resultsFilter (text: String) {
        list =  listItem.filter {
            it.name.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) || it.birthday.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
                    || it.pedigree.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) || it.pedigree.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
                    || it.gender.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) || it.contact?.toLowerCase(Locale.ROOT)?.contains(text.toLowerCase(Locale.ROOT)) ?: false
        }.toMutableList()
        val adapter = HomeAdapter(list as ArrayList<Model>)
        binding.recyclerView.adapter = adapter

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_search, menu)

        var searchItem = menu.findItem(R.id.action_search )
        var serachView  = searchItem.actionView as SearchView

        serachView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                Log.d("search", "Submit: $p0")
                if (p0 != null) {
                    resultsFilter(p0)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Log.d("search", "Text: $p0")
                if (p0 != null) {
                    resultsFilter(p0)
                }
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> resultsFilter("")
        }
        Log.d("item", "${item.itemId}")
        return super.onOptionsItemSelected(item)
    }
}
