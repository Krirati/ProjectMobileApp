package com.example.petlover.ui.home

import android.content.Intent
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.petlover.LoginActivity
import com.example.petlover.R
import kotlinx.android.synthetic.main.layout_list_item.view.*

class UserAdapter (private val modelItems: ArrayList<Model>): RecyclerView.Adapter<UserAdapter.ViewHolder>() {

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
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textViewName = itemView.findViewById(R.id.animalsName) as TextView
//        val imgPet = itemView.findViewById(R.id.imagePet) as ImageView
    }
}
