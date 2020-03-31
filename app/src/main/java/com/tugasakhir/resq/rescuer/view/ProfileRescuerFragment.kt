package com.tugasakhir.resq.rescuer.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.OnboardingActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.model.Rescuer
import kotlinx.android.synthetic.main.fragment_profile_rescuer.*

class ProfileRescuerFragment : Fragment() {

    private var role = ""

    companion object {
        const val ROLE = ""

        fun newInstance(isKorban: Boolean): ProfileRescuerFragment {
            val fragment = ProfileRescuerFragment()
            val bundle = Bundle()
            if (isKorban) {
                bundle.putString(ROLE, "victim")
            } else {
                bundle.putString(ROLE, "rescuer")
            }

            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!
        role = arguments?.getString(ROLE).toString()

        return inflater.inflate(R.layout.fragment_profile_rescuer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button_logout.setOnClickListener {
            button_logout.setTextColor(resources.getColor(R.color.white_light))
            button_logout.setBackgroundResource(R.drawable.shape_bordered_button_clicked)
            firebaseAuth.signOut()
            val intent = Intent(activity, OnboardingActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }

    }

    override fun onResume() {
        super.onResume()

        if (role == "rescuer") {
            val ref = FirebaseDatabase.getInstance().getReference("Rescuers").child(user.uid)
            val rescuerFragment = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val rescuer = p0.getValue(Rescuer::class.java)
                    textview_profile_name.text = rescuer?.name
                    textview_profile_rescuer_division.text = rescuer?.division
                    textview_profile_rescuer_phone.text = rescuer?.phone
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.wtf("ProfileFragmentError : ", p0.message)
                }

            }

            ref.addListenerForSingleValueEvent(rescuerFragment)

            button_profile_editprofile.setOnClickListener {
                val intent = Intent(activity, EditProfileRescuerActivity::class.java)
                startActivity(intent)
            }
        } else {
            textview_profile_name.text = "Nama korban"
        }
    }
}
