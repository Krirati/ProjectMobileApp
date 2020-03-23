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

class UserAdapter (private val modelItems: ArrayList<Model>): RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
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
//        holder.imgPet.set = model.timestamp.toString()
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
    }
    private fun editItem(view: View, model: Model) {
        Toast.makeText(view.context,"Edit ${model.name}",Toast.LENGTH_SHORT).show()
    }
    private fun deleteItem(view: View, model: Model) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Delete ${model.name}")
        builder.setMessage("Do you really want to delete it?")
        builder.setPositiveButton("Yes") { dialogInterface, i ->
            db.collection("animals").document(model.uid)
                .delete()
                .addOnSuccessListener { Log.d("Delete pet", "DocumentSnapshot successfully deleted!")
                    Snackbar.make(view,"Ok, delete success",Snackbar.LENGTH_SHORT).show()}
                .addOnFailureListener { e -> Log.w("Error delete pet", "Error deleting document", e)
                    Snackbar.make(view,"Fail, delete fail",Snackbar.LENGTH_SHORT).show()}

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
