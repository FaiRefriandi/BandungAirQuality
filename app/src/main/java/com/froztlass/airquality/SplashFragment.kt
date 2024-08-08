package com.froztlass.airquality

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class SplashFragment : Fragment() {
    // Fa'i Refriandi - 10121079
    // PemAndro3 - 08/08/2024
    // Tugas Besar UAS
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            if (onBoardingFinished()){
                findNavController().navigate(R.id.action_splashFragment2_to_homeFragment)
            }else{
                findNavController().navigate(R.id.action_splashFragment2_to_viewPagerFragment)
            }
        }, 3000)
    }

    private fun onBoardingFinished(): Boolean{
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished" ,false)
    }
}
