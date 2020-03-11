package com.example.petlover.ui.home

import android.content.Intent
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.petlover.LoginActivity
import com.example.petlover.R
import kotlinx.android.synthetic.main.layout_list_item.view.*

class HomeAdapter (private val modelItems: ArrayList<Model>): RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return modelItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: Model = modelItems[position]
        holder.textViewName.text = model.name
        holder.textContact.text = model.timestamp.toString()
//        holder.textViewAddress.text = model.category?.get(0)
//        holder.imgIcon.setImageResource(model.icon)
//        holder.imgStatus.setImageResource(model.status)
        when (model.gender) {
            "Male" -> holder.textGender.setImageResource(R.drawable.male)
            "Female" -> holder.textGender.setImageResource(R.drawable.female)
        }
        when (model.category?.get(0)) {
            "Find a couple" -> holder.imgStatus.setImageResource(R.drawable.love)
            "Lost animals" -> holder.imgStatus.setImageResource(R.drawable.love)
            "Find friends" -> holder.imgStatus.setImageResource(R.drawable.love)
            else -> holder.imgStatus.isInvisible
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        init {
            itemView.cardView.setOnClickListener {
                Toast.makeText(itemView.context,"Click",Toast.LENGTH_SHORT).show()
                val intent = Intent(itemView.context, LoginActivity::class.java)
                itemView.context.startActivity(intent)
            }
        }
        val textViewName = itemView.findViewById(R.id.text_name) as TextView
        val textGender = itemView.findViewById(R.id.gender) as ImageButton
        val textViewAddress = itemView.findViewById(R.id.place) as TextView
        val textContact = itemView.findViewById(R.id.contact) as TextView
        val imgIcon = itemView.findViewById(R.id.iconType) as ImageButton
        val imgStatus = itemView.findViewById(R.id.iconStatus) as ImageButton

    }
}
