package com.froztlass.airquality.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.froztlass.airquality.R

class SecondScreen : Fragment() {
    // Fa'i Refriandi - 10121079
    // PemAndro3 - 09/06/2024
    // Tugas Besar UTS
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_second_screen, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        val nextTextView = view.findViewById<TextView>(R.id.next_2)

        nextTextView.setOnClickListener {
            viewPager?.currentItem = 2
        }

        return view
    }
}