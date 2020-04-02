package com.example.petlover.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.example.petlover.R
import com.example.petlover.ui.model.Model
import com.example.petlover.ui.setting.SettingsActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import android.support.*
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.snapshot.Index
import kotlinx.android.synthetic.main.layout_list_item.view.*

import java.util.ArrayList

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val tabs = root.findViewById(R.id.tabs) as TabLayout
        val viewPager = root.findViewById(R.id.viewPager) as ViewPager
        val fragmentAdapter = MyPagerAdapter(childFragmentManager)
        viewPager.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewPager)
        setHasOptionsMenu(true)
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_search, menu)
<<<<<<< HEAD

        val searchItem = menu.findItem(R.id.action_search)
            if (searchItem != null){
            val searchView = searchItem.actionView as SearchView
            val db = FirebaseFirestore.getInstance()
            val  searchableList: MutableList<HomeAdapter.ViewHolder>
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d("search", query)
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d("search", newText)
//                    fun getData(itemView: View): Boolean {
//                        val textViewName = itemView.findViewById(R.id.text_name) as TextView
//                            Log.d("search", textViewName.text.toString())
//                        return true;
//    //                    val textGender = itemView.findViewById(R.id.gender) as ImageButton
//    //                    val textViewAddress = itemView.findViewById(R.id.place) as TextView
//    //                    val textContact = itemView.findViewById(R.id.contact) as TextView
//    //                    val imgPet = itemView.findViewById(R.id.imagePet) as ImageView
//    //                    val cardView = itemView.cardView as CardView
//    //                    val textPedigree = itemView.findViewById(R.id.pedigree) as TextView
//                    }
                    return true;
                }
            })
        }
=======
        val searchItem = menu.findItem(R.id.action_search)
//        if (searchItem != null){
//            val searchView = searchItem.actionView as SearchView
//            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//                override fun onQueryTextSubmit(query: String?): Boolean {
//                    return true
//                }
//
//                override fun onQueryTextChange(newText: String?): Boolean {
//                    val namePet: String = ""
//                    val db = FirebaseFirestore.getInstance()
//                    if(newText!!.isNotEmpty()){
//
//                    }else{
//
//
//                    }
//                    return true
//                }
//
//            })
//        }
>>>>>>> c74ce4ced98bfa3cbb484481b021ea06877be5fb


//        val item = menu.findItem(R.id.action_search)
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
<<<<<<< HEAD

=======
>>>>>>> c74ce4ced98bfa3cbb484481b021ea06877be5fb
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                val intent = Intent(context, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}