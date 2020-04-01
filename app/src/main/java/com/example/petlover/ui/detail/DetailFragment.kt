package com.example.petlover.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.databinding.DataBindingUtil
import com.example.petlover.Chatlog

import com.example.petlover.R
import com.example.petlover.RegisterActivity
import com.example.petlover.databinding.FragmentDetailBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.*
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment : Fragment() {
    private lateinit var uidreciveruser: String
    private lateinit var binding: FragmentDetailBinding
    private var isOpen: Boolean = false
    private lateinit var dbs : FirebaseStorage
    private val db = FirebaseFirestore.getInstance()
    private var latitude: Double=0.toDouble()
    private var longitude: Double=0.toDouble()
    private var userPost = ""
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
                    floatingMapButton.startAnimation(fabOpen)
                    floatingActionButton.startAnimation(antiFabClockwise)
                    floatingChatButton.startAnimation(fabOpen)
                    floatingChatButton.isClickable = true
                    floatingMapButton.isClickable = true
                    floatingActionButton.setImageResource(R.drawable.ic_unfold_less_black_24dp)
                    isOpen = true
                }
            }
            floatingMapButton.setOnClickListener {
                openGoogleMap()
            }
            floatingChatButton.setOnClickListener {
                checkchat()
            }
        }
        getDataPet ()
        return binding.root
    }
    private fun getDataPet () {
        val args = DetailFragmentArgs.fromBundle(arguments!!)
//        Toast.makeText(context,"Name Pet: ${args.petID}",Toast.LENGTH_SHORT).show()
//        var userPost = ""
        db.collection("animals")
            .document(args.petID)
            .get()
            .addOnSuccessListener {
                binding.apply {
                    nameAnimal.text = it.get("name").toString()
                    pedigree.text = it.get("pedigree").toString()
                    val latLng = it.get("latlng").toString().split(",")
                    val latitudeLongitude = it.get("latlng") as Map<*, *>
                    latitude = latitudeLongitude["latitude"] as Double
                    longitude = latitudeLongitude["longitude"] as Double
                    textViewPlacr.text =  "${latLng[0].split("{")[1]}\n${latLng[1].split("}")[0]}"
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
                    Picasso.get()
                        .load("${it.get("imageUID")}")
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .rotate(90F)
                        .into(imageViewPet)
                    userPost = it.get("uidUser").toString()
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
    }

    private fun checkchat(){
        var count = 0
        var useruid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var uiduser:String
        val args = DetailFragmentArgs.fromBundle(arguments!!)
        db.collection("animals")
            .document(args.petID)
            .get().addOnSuccessListener {
                uiduser = it.get("uidUser") as String
                Log.d("uiduser",uiduser)
                Log.d("uiduser",useruid)
                db.collection("chat")
                    .whereIn("uidreciver", listOf(uiduser, useruid))
                    .get()
                    .addOnSuccessListener { reciver ->
                        Log.d("hh","Hello error")
                    for (uid in reciver){
                        if ((uid["uidreciver"] == uiduser && uid["uidsender"] == useruid) || (uid["uidreciver"] == useruid && uid["uidsender"] == uiduser)){
                            val intent = Intent(context, Chatlog::class.java).putExtra("uidRoom",uid.id)
                            startActivity(intent)
                            Log.d("readdata", uid.id)
                            count = 1
                        }
                    }
                        Log.d("count",count.toString())
                        if (count != 1){
                            var database = FirebaseDatabase.getInstance()
                            val randuid = database.reference.push().key
                            Log.d("uuu",randuid)
                            val newroom = hashMapOf(
                                "uidsender" to useruid,
                                "uidreciver" to uiduser,
                                "timestamp" to FieldValue.serverTimestamp()
                            )
                            if (randuid != null) {
                                db.collection("chat").document(randuid).set(newroom)
                                val intent = Intent(context, Chatlog::class.java).putExtra("uidRoom",randuid)
                                startActivity(intent)
                            }
                        }

                }
            }
    }
    private fun openGoogleMap () {
        Toast.makeText(context,"YOU click Chat ${latitude},${longitude}",Toast.LENGTH_SHORT).show()
        val gmmIntentUri = Uri.parse("google.navigation:q=${latitude},${longitude}&mode=d");
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
