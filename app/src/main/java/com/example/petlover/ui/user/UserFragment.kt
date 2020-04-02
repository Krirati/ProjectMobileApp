package com.example.petlover.ui.user

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager

import com.example.petlover.R
import com.example.petlover.databinding.FragmentUserBinding

import com.example.petlover.ui.model.Model
import com.example.petlover.ui.home.UserAdapter
import com.example.petlover.ui.setting.SettingsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user.*

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
        binding.apply {
            floatingActionButton.setOnClickListener {
//                Navigation.findNavController(it).navigate(R.id.action_navigation_user_to_addFragment2)
                Navigation.findNavController(it).navigate(UserFragmentDirections
                    .actionNavigationUserToAddFragment2("ADD","","0","0"))
            }
            recyclerListAnimals.layoutManager = GridLayoutManager(context,1,GridLayoutManager.VERTICAL,false)
            swipeRefreshLayoutUser.setOnRefreshListener {
                Handler().postDelayed({
                    listAnimals.clear()
                    getListPet()
                    swipeRefreshLayoutUser.isRefreshing = false
                },3000)
            }
            swipeRefreshLayoutUser.setColorSchemeColors(
                Color.parseColor("#008744")
                , Color.parseColor("#0057e7"), Color.parseColor("#d62d20"))
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getUser()
        getListPet()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setHasOptionsMenu(true)
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
                val intent = Intent(context, SettingsActivity::class.java)
                startActivity(intent)
//                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun getUser () {
        if (auth.currentUser?.displayName.isNullOrEmpty()) {
            db.collection("users")
                .document("${auth.currentUser?.uid}")
                .get()
                .addOnSuccessListener {documents->
                    Log.d("Data in user ","${documents.id} => ${documents.data}")
                    binding.apply {
//                        val fullname = documents.get("email").toString().split("@")
                        name.text = documents.get("username").toString()
                        email.text = documents.get("email").toString()
                        Picasso.get()
                            .load("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png")
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(imgProfile)
                    }
                }
        } else {
            binding.apply {
                name.text = auth.currentUser!!.displayName
                email.text = auth.currentUser!!.email
                Picasso.get()
                    .load("${auth.currentUser?.photoUrl}")
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imgProfile)
                contact.text = auth.currentUser!!.phoneNumber
            }
        }
    }
    private fun getListPet () {
        listAnimals.clear()
        countPost = 0
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
                binding.apply {
                    recyclerListAnimals.adapter = adapter
                    progressBarUser.visibility = View.INVISIBLE
                }
//                binding.numPost.text = countPost.toString()
//                binding.numFriend.text = countPost.toString()
            }
            .addOnFailureListener { exception ->
                Log.w("Data in animals", "Error getting documents.", exception)
            }
    }
}
