package com.tugasakhir.resq.rescuer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.fragment_profile_rescuer.*

class ProfileRescuerFragment : Fragment() {

    companion object {
        fun newInstance(): ProfileRescuerFragment = ProfileRescuerFragment()
    }

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user : FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!

        return inflater.inflate(R.layout.fragment_profile_rescuer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        test_email_profile_rescuer.text = user.email.toString()
        test_name_profile_rescuer.text = "Halo"
        button_logout.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(activity, SignInRescuerActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }

    }
}
