package com.froztlass.airquality.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.froztlass.airquality.R
import com.froztlass.airquality.onboarding.screens.FirstScreen
import com.froztlass.airquality.onboarding.screens.SecondScreen
import com.froztlass.airquality.onboarding.screens.ThirdScreen
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ViewPagerFragment : Fragment() {
    // Fa'i Refriandi - 10121079
    // PemAndro3 - 08/08/2024
    // Tugas Besar UAS
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen(),
        )

        // Adapter buat ViewPager
        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        // Cari ViewPager sama set adapter
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = adapter


        return view
    }
}
