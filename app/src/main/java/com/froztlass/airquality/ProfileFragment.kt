package com.froztlass.airquality.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.froztlass.airquality.CircleBorderTransformation
import com.froztlass.airquality.R
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val nameTextView = view.findViewById<TextView>(R.id.profile_name)
        val emailTextView = view.findViewById<TextView>(R.id.profile_email)
        val profileImageView = view.findViewById<ImageView>(R.id.profile_image)

        if (user != null) {
            nameTextView.text = user.displayName
            emailTextView.text = user.email

            //Load buat border di profile
            Glide.with(this)
                .load(user.photoUrl)
                .apply(RequestOptions.bitmapTransform(CircleBorderTransformation(10f, resources.getColor(R.color.bluetak))))
                .placeholder(R.drawable.real_profile)
                .into(profileImageView)
        }

        return view
    }
}
