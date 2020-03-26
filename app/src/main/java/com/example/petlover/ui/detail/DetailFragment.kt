package com.example.petlover.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.petlover.Chatlog

import com.example.petlover.R
import com.example.petlover.RegisterActivity
import com.example.petlover.databinding.FragmentDetailBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment : Fragment() {
    private lateinit var uidreciveruser: String
    private lateinit var binding: FragmentDetailBinding
    private var isOpen: Boolean = false
    private lateinit var dbs : FirebaseStorage
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container,false)
        val fabOpen = AnimationUtils.loadAnimation(context,R.anim.fab_open)
        val fabClose = AnimationUtils.loadAnimation(context,R.anim.fab_close)
        val fabClockwise = AnimationUtils.loadAnimation(context,R.anim.rotate_clockwise)
        val antiFabClockwise = AnimationUtils.loadAnimation(context,R.anim.rotate_clockwise)
        binding.apply {
            floatingActionButton.setOnClickListener{
                if(isOpen) {
                    floatingChatButton.startAnimation(fabClose)
                    floatingMapButton.startAnimation(fabClose)
                    floatingActionButton.startAnimation(fabClockwise)
                    floatingChatButton.isClickable = false
                    floatingMapButton.isClickable = false
                    floatingActionButton.setImageResource(R.drawable.ic_unfold_more_black_24dp)
                    isOpen = false
                } else {
                    floatingChatButton.startAnimation(fabOpen)
                    floatingMapButton.startAnimation(fabOpen)
                    floatingActionButton.startAnimation(antiFabClockwise)
                    floatingChatButton.isClickable = true
                    floatingMapButton.isClickable = true
                    floatingActionButton.setImageResource(R.drawable.ic_unfold_less_black_24dp)
                    isOpen = true
                }
            }
            floatingMapButton.setOnClickListener {
                Toast.makeText(context,"YOU click map",Toast.LENGTH_SHORT).show()
            }
            floatingChatButton.setOnClickListener {
                checkchat()
                Toast.makeText(context,"YOU click Chat",Toast.LENGTH_SHORT).show()
            }
        }
        getDataPet ()
        return binding.root
    }
    private fun getDataPet () {
        val args = DetailFragmentArgs.fromBundle(arguments!!)
//        Toast.makeText(context,"Name Pet: ${args.petID}",Toast.LENGTH_SHORT).show()
        var userPost = ""
        db.collection("animals")
            .document(args.petID)
            .get()
            .addOnSuccessListener {
                binding.apply {
                    nameAnimal.text = it.get("name").toString()
                    pedigree.text = it.get("pedigree").toString()
                    textViewPlacr.text = it.get("place").toString()
                    textViewBirthday.text = it.get("birthday").toString()
                    textViewCall.text = it.get("contact").toString()
                    val timestamp = it["timestamp"] as com.google.firebase.Timestamp
                    val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                    val sdf = SimpleDateFormat("MM/dd/yyyy")
                    val netDate = Date(milliseconds)
                    val date = sdf.format(netDate).toString()
                    time.text = date
                    when (it.get("gender").toString()) {
                        "Male" -> gender2.setImageResource(R.drawable.male)
                        "Female" -> gender2.setImageResource(R.drawable.female)
                    }
                    userPost = it.get("uidUser") as String
                    db.collection("users")
                        .document(userPost)
                        .get()
                        .addOnSuccessListener {
                            binding.apply {
                                user.text = it.get("email").toString()
                                if (textViewCall.text == null) textViewCall.text = it.get("contact").toString()
                            }
                        }
                }
            }
            .addOnFailureListener {
                view?.let { it1 -> Snackbar.make(it1,"Load fail",Snackbar.LENGTH_SHORT).show() }
            }
        Toast.makeText(context,"Name user: ${userPost}",Toast.LENGTH_SHORT).show()


    }
    private fun chatcreateroom(uidreciver:String){
        val intent = Intent(context, RegisterActivity::class.java).putExtra("uidreciver",uidreciver)
        startActivity(intent)
    }
    private fun checkchat(){
        var uiduser:String
        val args = DetailFragmentArgs.fromBundle(arguments!!)
        db.collection("animals")
            .document(args.petID)
            .get().addOnSuccessListener {
                uiduser = it.get("uidUser") as String
                Log.d("uiduser",uiduser)
                db.collection("chat").get().addOnSuccessListener { reciver ->
                    for (uid in reciver){
                        Log.d("readdata", uid.id)
                        if(uiduser == uid["uidsender"]){
                            val intent = Intent(context, Chatlog::class.java).putExtra("uidRoom",uid.id)
                            startActivity(intent)
                            break
                        }
                        Log.d("readdata",uid["status"].toString())
                        /*if (uid["uidreciver"].toString() == uidreciver){

                        }*/
                    }
                }
            }


    }
}
