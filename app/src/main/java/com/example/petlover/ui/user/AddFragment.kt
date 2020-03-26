package com.example.petlover.ui.user

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.petlover.R
import com.example.petlover.databinding.ActivityAddpetBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.layout_list_item.*
import java.util.*
import kotlin.collections.ArrayList

class AddFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var selectedPhotoUri: Uri? = null
    private var filenameImg: String? = null
    private val list = ArrayList<String>()
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    private var uriImage: String? = null
    private lateinit var binding: ActivityAddpetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val view: View = inflater.inflate(R.layout.fragment_user, container, false)
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
                    chip.setChipBackgroundColorResource(R.color.greyBackground)
                }
                if (list.isNotEmpty()){
                    // SHow the selection
//                    Toast.makeText(view.context,"Selected $list",Toast.LENGTH_SHORT).show()
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
        binding.apply {
            buttonAdd.isEnabled = false
            buttonAdd.isClickable = false
            progressBar.visibility = View.VISIBLE
        }
        val name = binding.name.text.toString()
        val pedigree = binding.pedigree.text.toString()
        val birthday = binding.birthday.text.toString()
        val genderId = binding.radioGroupGender.checkedRadioButtonId
        val genderString = resources.getResourceEntryName(genderId)
        val generateId = db.collection("animals").document().id
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val contact = binding.inputContact.text.toString()
        filenameImg = UUID.randomUUID().toString()
        if (list.size == 0) {list.add("Find a couple")}
        val pet = hashMapOf(
            "name" to name,
            "pedigree" to pedigree,
            "birthday" to birthday,
            "gender" to genderString,
            "imageUID" to uriImage,
            "category" to list,
            "contact" to contact,
            "uid" to generateId,
            "uidUser" to user,
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
                Snackbar.make(view,"Upload successfully",Snackbar.LENGTH_SHORT).show()
                clearField()
            }
            .addOnFailureListener { e ->
                Log.w("Add pet error", "Error adding document", e)
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
}