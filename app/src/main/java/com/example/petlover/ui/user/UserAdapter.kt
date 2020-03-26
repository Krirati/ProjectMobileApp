package com.example.petlover.ui.home

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.petlover.R
import com.example.petlover.ui.model.Model
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class UserAdapter (private val modelItems: ArrayList<Model>): RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val dbImg = FirebaseStorage.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_animals, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return modelItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: Model = modelItems[position]
        holder.textViewName.text = model.name
        Picasso.get()
            .load("${model.imageUID}")
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.imgPet)
        holder.btnEdit.setOnClickListener { view ->
            editItem(view, model)
        }
        holder.btnDelete.setOnClickListener { view ->
            deleteItem(view, model)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textViewName = itemView.findViewById(R.id.animalsName) as TextView
        val btnEdit = itemView.findViewById(R.id.btnEdit) as ImageButton
        val btnDelete = itemView.findViewById(R.id.btnDelete) as ImageButton
        val imgPet = itemView.findViewById(R.id.imagePet) as ImageView
    }
    private fun editItem(view: View, model: Model) {
        Toast.makeText(view.context,"Edit ${model.name}",Toast.LENGTH_SHORT).show()
    }
    private fun deleteItem(view: View, model: Model) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Delete ${model.name}")
        builder.setMessage("Do you really want to delete it?")
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            db.collection("animals").document(model.uid)
                .delete()
                .addOnSuccessListener { Log.d("Delete pet", "DocumentSnapshot successfully deleted!")
                    Snackbar.make(view,"Done, delete success",Snackbar.LENGTH_SHORT).show()}
                .addOnFailureListener { e -> Log.w("Error delete pet", "Error deleting document", e)
                    Snackbar.make(view,"Fail, delete fail",Snackbar.LENGTH_SHORT).show()}
            dbImg.reference
                .child("${model.imgRef}")
                .delete()
                .addOnSuccessListener {
                    Log.d("Delete pet", "DocumentSnapshot successfully deleted!")
                   }
                .addOnFailureListener { e ->
                    Log.w("Error delete pet", "Error deleting document", e)
                    }
        }
        builder.setNegativeButton("No") {dialogInterface, i ->
            Snackbar.make(view,"Ok, You cancelled the delete",Snackbar.LENGTH_SHORT).show()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#43a047"))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
    }
}
