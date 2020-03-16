package com.example.petlover.ui.user

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.petlover.AddActivity

import com.example.petlover.R
import com.example.petlover.databinding.FragmentUserBinding

import com.example.petlover.ui.home.Model
import com.example.petlover.ui.home.UserAdapter
import com.example.petlover.ui.setting.SettingsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserFragment : Fragment() {

    private lateinit var viewModel: UserViewModel
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var listAnimals = ArrayList<Model>()
    private lateinit var binding: FragmentUserBinding
    private var countPost: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user,container,false)
        binding.floatingActionButton.setOnClickListener {

            Navigation.findNavController(it).navigate(R.id.action_navigation_user_to_addActivity)
        }
        getUser()
        binding.recyclerListAnimals.layoutManager = GridLayoutManager(context,1,GridLayoutManager.VERTICAL,false)
        getListPet()
        return binding.root
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_setting, menu)
        val item = menu.findItem(R.id.action_setting)
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_setting -> {
                view?.let { Navigation.findNavController(it).navigate(R.id.action_navigation_user_to_editUserFragment) }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun getUser () {
        db.collection("users").document("${auth.currentUser?.uid}")
            .get()
            .addOnSuccessListener {documents->
                Log.d("Data in user ","${documents.id} => ${documents.data}")
                binding.name.text = documents.get("email").toString()
            }
    }
    private fun getListPet () {
        db.collection("animals")
            .whereEqualTo("uidUser","${auth.currentUser?.uid}")
            .get()
            .addOnSuccessListener { documents->
                for (document in documents) {
                    Log.d("Data in user animal","${document.id} => ${document.data}")
                    val data = document.toObject(Model::class.java)
                    listAnimals.add(data)
                    countPost++
                }
                val adapter = UserAdapter(listAnimals)
                binding.recyclerListAnimals.adapter = adapter
                binding.numPost.text = countPost.toString()
                binding.numFriend.text = countPost.toString()
            }
            .addOnFailureListener { exception ->
                Log.w("Data in animals", "Error getting documents.", exception)
            }
    }
}
