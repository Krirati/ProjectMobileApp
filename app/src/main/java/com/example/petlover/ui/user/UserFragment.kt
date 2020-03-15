package com.example.petlover.ui.user

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petlover.AddActivity

import com.example.petlover.R

import com.example.petlover.ui.home.Model
import com.example.petlover.ui.home.UserAdapter
import com.example.petlover.ui.setting.SettingsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chatlog.*

class UserFragment : Fragment() {

    private lateinit var viewModel: UserViewModel
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var listAnimals = ArrayList<Model>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_user, container, false)
        val btnadd = view.findViewById(R.id.buttonAdd) as Button
        btnadd.setOnClickListener {
            val intent = Intent(context, AddActivity::class.java)
            startActivity(intent)
        }
        val recycleViewAnimals = view.findViewById(R.id.recyclerListAnimals) as RecyclerView
        recycleViewAnimals.layoutManager = GridLayoutManager(context,1,GridLayoutManager.VERTICAL,false)
//        val chat = ArrayList<Model>()
//        chat.add(Model("dog"))
//        chat.add(Model("cat"))
//        chat.add(Model("ant"))
//        chat.add(Model("bird"))
//        chat.add(Model("bird"))
//        val adapter = UserAdapter(chat)
//        recycleViewAnimals.setHasFixedSize(true)
//        recycleViewAnimals.adapter = adapter
        getListPet(recycleViewAnimals)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        menu.clear()
//        inflater.inflate(R.menu.menu_setting, menu)
//        val item = menu.findItem(R.id.action_setting)
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.action_setting -> {
//                val intent = Intent(context, SettingsActivity::class.java)
//                startActivity(intent)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
    private fun getListPet (recyclerListAnimals: RecyclerView) {
        db.collection("animals")
            .whereEqualTo("uidUser","${auth.currentUser?.uid}")
            .get()
            .addOnSuccessListener { documents->
                for (document in documents) {
                    Log.d("Data in user animal","${document.id} => ${document.data}")
                    val data = document.toObject(Model::class.java)
                    listAnimals.add(data)
                }
                val adapter = UserAdapter(listAnimals)
                recyclerListAnimals.adapter = adapter

            }
            .addOnFailureListener { exception ->
                Log.w("Data in animals", "Error getting documents.", exception)
            }
    }
}
