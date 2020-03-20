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
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.model.Rescuer
import kotlinx.android.synthetic.main.fragment_profile_rescuer.*

class ProfileRescuerFragment : Fragment() {

    companion object {
        fun newInstance(): ProfileRescuerFragment =
            ProfileRescuerFragment()
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

//        val ref = FirebaseDatabase.getInstance().getReference("Rescuers").child(user.uid)

//        test_email_profile_rescuer.text = user.email.toString()

//        val rescuerListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val rescuer = dataSnapshot.getValue(Rescuer::class.java)
//                test_name_profile_rescuer.text = rescuer?.name
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//                Log.d("PROFILE : ", p0.message)
//            }
//        }
//
//        ref.addListenerForSingleValueEvent(rescuerListener)

        button_logout.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(activity, SignInRescuerActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }

    }
}
