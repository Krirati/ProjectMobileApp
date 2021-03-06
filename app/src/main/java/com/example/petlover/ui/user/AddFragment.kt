package com.example.petlover.ui.user

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.maps.model.LatLng
import com.example.petlover.R
import com.example.petlover.databinding.ActivityAddpetBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class AddFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val dbImg = FirebaseStorage.getInstance()
    private var selectedPhotoUri: Uri? = null
    private var filenameImg: String? = null
    private val list = ArrayList<String>()
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    private var uriImage: String? = null
    private lateinit var binding: ActivityAddpetBinding
    private var gender = ""

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.activity_addpet,container,false)
        binding.apply {
            buttonAdd.setOnClickListener {
                crateNewPet(it)
            }
            input.setOnClickListener {
                showDatePickerDialog()
            }
            birthday.setOnClickListener{
                showDatePickerDialog()
            }
            location.setOnClickListener {
                val args = AddFragmentArgs.fromBundle(arguments!!)
                Navigation.findNavController(it).navigate(AddFragmentDirections.actionAddFragmentToMapsFragment(args.events, args.uidPet,args.latitude,args.longitude))
            }
            floatingSelectImg.setOnClickListener {
                Log.d("AddActivity", "select img")
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent,0)
            }
            btnCancel.setOnClickListener {
                clearField()
            }
        }
        for (index in 0 until binding.chipGroup.childCount) {
            val chip:Chip = binding.chipGroup.getChildAt(index) as Chip

            // Set the chip checked change listener
            chip.setOnCheckedChangeListener{view, isChecked ->
                if (isChecked){
                    list.add(view.text.toString())
                    chip.setChipBackgroundColorResource(R.color.colorPrimary)
                }else{
                    list.remove(view.text.toString())
                    chip.setChipBackgroundColorResource(R.color.btnChip)
                }
//                if (list.isNotEmpty()){
                    // SHow the selection
//                    Toast.makeText(view.context,"Selected $list",Toast.LENGTH_SHORT).show()
//                }
            }
        }
        for (index in 0 until binding.chipGroupGender.childCount) {
            val chip:Chip = binding.chipGroupGender.getChildAt(index) as Chip

            // Set the chip checked change listener
            chip.setOnCheckedChangeListener{view, isChecked ->
                if (isChecked){
                    gender = view.text.toString()
                    chip.setChipBackgroundColorResource(R.color.colorPrimary)
                }else{
                    gender = ""
                    chip.setChipBackgroundColorResource(R.color.btnChip)
                }
            }
        }

        val args = AddFragmentArgs.fromBundle(arguments!!)
        when (args.events) {
            "UPDATE" -> {
                (activity as AppCompatActivity).supportActionBar?.title = "Update description"
                binding.buttonAdd.text = "Update"
                db.collection("animals")
                    .document(args.uidPet)
                    .get()
                    .addOnSuccessListener {
                        binding.apply {
                            name.setText(it.get("name").toString())
                            pedigree.setText(it.get("pedigree").toString())
                            birthday.setText(it.get("birthday").toString())
//                            binding.radioGroupGender.checkedRadioButtonId
                            inputContact.setText(it.get("contact").toString())
//                            when (it.get("gender")) {
//                                "Male" -> radioGroupGender.check(R.id.Male)
//                                "Female"-> radioGroupGender.check(R.id.Female)
//                            }
                            when (it.get("gender")) {
                                "Male" -> chipGroupGender.check(R.id.MaleChip)
                                "Female" -> chipGroupGender.check(R.id.FemaleChip)
                            }
                            val chipSelect = it.get("category") as ArrayList<*>
                                for ( i in chipSelect.indices) {
                                    chipGroup.check(
                                        when (chipSelect[i]) {
                                            "Find a couple" -> R.id.chipCouple
                                            "Find friends" -> R.id.chipFriends
                                            "Lost animals" -> R.id.chipLost
                                            else -> -1
                                       }
                                    )
                                }
                            if (args.latitude.toDouble() !== 0.0) {
                                location.setText("${args.latitude},${args.longitude}").toString()
                            } else {
                                val latLng = it.get("latlng") as Map<*, *>
                                location.setText("${latLng["latitude"]},${latLng["longitude"]}")
                            }
                            imageView.visibility = View.VISIBLE
                            Picasso.get()
                                .load("${it.get("imageUID")}")
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .into(imageView)
                        }
                    }
                    .addOnFailureListener {
                        view?.let { it1 -> Snackbar.make(it1,"Get data fail",Snackbar.LENGTH_SHORT).show() }
                    }
            }
            else -> {
                binding.apply {
                    if (args.latitude.isNotEmpty()) {
                        inputLocation.hint = "${args.latitude},${args.longitude}"
                        inputLocation.defaultHintTextColor = ColorStateList.valueOf(android.graphics.Color.BLACK)
                    }
                }
            }
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("Add pet ", "Photo was selected")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            binding.imageView.visibility = View.VISIBLE
            binding.imageView.setBackgroundDrawable(bitmapDrawable)
        }
    }
    private fun crateNewPet (view: View) {
        val args = AddFragmentArgs.fromBundle(arguments!!)
        val name = binding.name.text.toString()
        val pedigree = binding.pedigree.text.toString()
        val birthday = binding.birthday.text.toString()
        val generateId = db.collection("animals").document().id
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val contact = binding.inputContact.text.toString()
        filenameImg = UUID.randomUUID().toString()
        val genderString = gender
        if (list.size == 0) {list.add("Find a couple")}
        if (!name.isNullOrEmpty() && !pedigree.isNullOrEmpty() && !birthday.isNullOrEmpty() && list.size>0) {
            when (args.events) {
                "ADD" -> {
                    binding.apply {
                        buttonAdd.isEnabled = false
                        buttonAdd.isClickable = false
                        progressBar.visibility = View.VISIBLE
                    }
                    val latlng = LatLng(args.latitude.toDouble(),args.longitude.toDouble())
                    val pet = hashMapOf(
                        "name" to name,
                        "pedigree" to pedigree,
                        "birthday" to birthday,
                        "gender" to gender,
                        "imageUID" to uriImage,
                        "category" to list,
                        "contact" to contact,
                        "uid" to generateId,
                        "uidUser" to user,
                        "latlng" to latlng,
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                    db.collection("animals").document(generateId).set(pet)
                        .addOnSuccessListener {
                            Log.d("Add pet done", "DocumentSnapshot added with ID: $generateId")
                            uploadImageToFirebaseStorage(generateId)
                            binding.apply {
                                buttonAdd.isEnabled = true
                                buttonAdd.isClickable = true
                                progressBar.visibility = View.GONE
                            }
                            Snackbar.make(view,"Upload successfully",Snackbar.LENGTH_SHORT)
                                .setAction("OK") {}
                                .setActionTextColor(Color.GREEN)
                                .show()
                            clearField()
                        }
                        .addOnFailureListener { e ->
                            Log.w("Add pet error", "Error adding document", e)
                        }
                }
                "UPDATE" -> {
                    binding.apply {
                        buttonAdd.isEnabled = false
                        buttonAdd.isClickable = false
                        progressBar.visibility = View.VISIBLE
                        deleteImage(args.uidPet)
                        val latlng = location.text.toString().split(",")
                        val latitudeLongitude = LatLng(latlng[0].toDouble(),latlng[1].toDouble())
                        val petUpdate = hashMapOf(
                            "name" to name,
                            "pedigree" to pedigree,
                            "birthday" to birthday,
                            "gender" to genderString,
                            "imageUID" to uriImage,
                            "category" to list,
                            "latlng" to latitudeLongitude,
                            "contact" to contact
                        )
                        db.collection("animals").document(args.uidPet)
                            .update(petUpdate as Map<String, Any>)
                            .addOnSuccessListener {
                                uploadImageToFirebaseStorage(args.uidPet)
                                buttonAdd.isEnabled = true
                                buttonAdd.isClickable = true
                                progressBar.visibility = View.GONE
                                Snackbar.make(view,"Update successfully",Snackbar.LENGTH_SHORT)
                                    .setAction("OK") {}
                                    .setActionTextColor(Color.GREEN)
                                    .show()
                            }
                            .addOnFailureListener { e ->
                                Log.w("UpdatePet", "Error adding document", e)
                                Snackbar.make(view,"Update fail try again",Snackbar.LENGTH_SHORT)
                                    .setAction("Fail") {}
                                    .setActionTextColor(Color.RED)
                                    .show()
                            }
                    }

                }
            }
        } else {
            Snackbar.make(view,"Please fill out all information.",Snackbar.LENGTH_SHORT)
                .setAction("Fail") {}
                .setActionTextColor(Color.RED)
                .show()
        }
    }

    private fun uploadImageToFirebaseStorage(generateId: String) {
        Log.w("Add pet image", "uploadImageToFirebaseStorage")
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Add pet img done", "Successfully uploaded image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("add img","File Location: $it")
                    db.collection("animals").document(generateId)
                        .update("imageUID", "${it}")
                        .addOnSuccessListener { Log.d("update", "DocumentSnapshot $it")}
                }
                db.collection("animals").document(generateId)
                    .update("imgRef", "${it.metadata?.path}")
                    .addOnSuccessListener { Log.d("update", "DocumentSnapshot $it")}
            }
            .addOnFailureListener{e ->
                Log.w("Add pet img error", "Error adding document", e)
            }
    }

    private fun clearField () {
        binding.apply {
            name.text?.clear()
            pedigree.text?.clear()
            birthday.text?.clear()
            location.text?.clear()
            imageView.visibility = View.GONE
            location.text?.clear()
            inputContact.text?.clear()
        }
        selectedPhotoUri = null
    }

    private fun showDatePickerDialog () {
        val datePickerDialog = DatePickerDialog(context,DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
            binding.birthday.setText("${mDay}/${mMonth}/${mYear}")
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun deleteImage (uidPet: String) {
        db.collection("animals")
            .document(uidPet)
            .get()
            .addOnSuccessListener {
                dbImg.reference
                    .child("${it.get("imageUID")}")
                    .delete()
                    .addOnSuccessListener {
                        Log.d("Delete pet", "DocumentSnapshot successfully deleted!")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Error delete pet", "Error deleting document", e)
                    }
            }

    }
}