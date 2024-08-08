package com.froztlass.airquality

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.froztlass.airquality.onboarding.screens.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MenuFragment : Fragment() {
    // Fa'i Refriandi - 10121079
    // PemAndro3 - 08/08/2024
    // Tugas Besar UAS
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        bottomNavigationView = view.findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navHome -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.navMaps -> {
                    replaceFragment(MapsFragment())
                    true
                }

                R.id.navTips -> {
                    replaceFragment(TipsFragment())
                    true
                }
                R.id.navProfile -> {
                    replaceFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
        replaceFragment(HomeFragment())

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}