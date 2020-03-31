package com.example.petlover.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petlover.R
import com.example.petlover.ui.model.Model
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.squareup.picasso.PicassoProvider
import kotlinx.android.synthetic.main.layout_list_item.view.*

class HomeAdapter (private val modelItems: ArrayList<Model>): RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    val namePet: String = ""
    val dbStorage = FirebaseStorage.getInstance()
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
        holder.textContact.text = model.contact
        holder.textViewAddress.text = model.uid
        when (model.gender) {
            "Male" -> holder.textGender.setImageResource(R.drawable.male)
            "Female" -> holder.textGender.setImageResource(R.drawable.female)
        }
        holder.cardView.setOnClickListener {
            val petId = model.uid
            Navigation.findNavController(it).navigate(HomeFragmentDirections
                .actionNavigationHomeToDetailFragment(petId))
        }
        Picasso.get()
            .load("${model.imageUID}")
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.imgPet)
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textViewName = itemView.findViewById(R.id.text_name) as TextView
        val textGender = itemView.findViewById(R.id.gender) as ImageButton
        val textViewAddress = itemView.findViewById(R.id.place) as TextView
        val textContact = itemView.findViewById(R.id.contact) as TextView
        val imgPet = itemView.findViewById(R.id.imagePet) as ImageView
        val cardView = itemView.cardView as CardView
    }
}
