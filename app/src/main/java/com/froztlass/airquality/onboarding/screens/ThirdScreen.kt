package com.froztlass.airquality.onboarding.screens

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.froztlass.airquality.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class ThirdScreen : Fragment() {
    // Fa'i Refriandi - 10121079
    // PemAndro3 - 09/06/2024
    // Tugas Besar UTS

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third_screen, container, false)

        val signInButton = view.findViewById<com.google.android.gms.common.SignInButton>(R.id.sign_in_button)

        // Initialize Firebase Auth and Google Sign-In
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Set click listener for Google Sign-In button
        signInButton.setOnClickListener {
            signInWithGoogle()
        }

        return view
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userName = user?.displayName
                    if (userName != null) {
                        saveUserNameToPreferences(userName)
                    }
                    Toast.makeText(requireContext(), "Welcome $userName", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_viewPagerFragment_to_homeFragment)
                    onBoardingFinished()
                } else {
                    Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserNameToPreferences(userName: String) {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("UserName", userName)
        editor.apply()
    }

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}
