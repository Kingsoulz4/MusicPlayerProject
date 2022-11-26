package com.example.musicplayerproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicplayerproject.R
import com.example.musicplayerproject.adapters.HomeItemAdapter
import com.example.musicplayerproject.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var fragmentHomeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        //var sliderItemAdapter = HomeItemAdapter(this)
        return fragmentHomeBinding.root
    }
}