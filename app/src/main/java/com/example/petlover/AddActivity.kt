package com.example.petlover

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.petlover.databinding.ActivityAddpetBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_addpet.*
import java.util.*

class AddActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var selectedPhotoUri: Uri? = null
    private var filenameImg: String? = null
    private val list = mutableListOf<String>()

    private lateinit var binding: ActivityAddpetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_addpet)
        binding.buttonAdd.setOnClickListener {
            crateNewPet(it)
        }
        floating_select_img.setOnClickListener {
            Log.d("AddActivity", "select img")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

        // Loop through the chips
        for (index in 0 until chipGroup.childCount) {
            val chip:Chip = chipGroup.getChildAt(index) as Chip

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
                    Toast.makeText(this,"Selected $list",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("Add pet ", "Photo was selected")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            binding.imageView.visibility = View.VISIBLE
            imageView.setBackgroundDrawable(bitmapDrawable)
        }
    }
    private fun crateNewPet (view: View) {
        binding.apply {
            buttonAdd.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
        val name = findViewById<TextInputEditText>(R.id.name).text.toString()
        val pedigree = findViewById<TextInputEditText>(R.id.pedigree).text.toString()
        val birthday = findViewById<TextInputEditText>(R.id.birthday).text.toString()
        val genderId = radioGroupGender.checkedRadioButtonId
        val genderString = resources.getResourceEntryName(genderId)
        val generateId = db.collection("animals").document().id
        val user = FirebaseAuth.getInstance().currentUser?.uid
        filenameImg = UUID.randomUUID().toString()
        val pet = hashMapOf(
            "name" to name,
            "pedigree" to pedigree,
            "birthday" to birthday,
            "gender" to genderString,
            "imageUID" to filenameImg,
            "category" to list,
            "uid" to generateId,
            "uidUser" to user
        )
        db.collection("animals").document(generateId).set(pet)
            .addOnSuccessListener {
                Log.d("Add pet done", "DocumentSnapshot added with ID: $generateId")
                uploadImageToFirebaseStorage()
                binding.apply {
                    buttonAdd.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
                Snackbar.make(view,"Upload successfully",Snackbar.LENGTH_SHORT).show()
                clearField()
            }
            .addOnFailureListener { e ->
                Log.w("Add pet error", "Error adding document", e)
            }
    }

    private fun uploadImageToFirebaseStorage() {
        Log.w("Add pet image", "uploadImageToFirebaseStorage")
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Add pet img done", "Successfully uploaded image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("add img","File Location: $it")
                }
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
        }
    }
}