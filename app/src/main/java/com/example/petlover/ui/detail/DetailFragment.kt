package com.example.petlover.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.example.petlover.R
import com.example.petlover.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private var isOpen: Boolean = false
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
                Toast.makeText(context,"YOU click Chat",Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
}
