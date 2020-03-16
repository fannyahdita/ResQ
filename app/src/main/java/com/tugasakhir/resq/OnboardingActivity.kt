package com.tugasakhir.resq

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tugasakhir.resq.rescuer.view.SignInRescuerActivity
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        if(firebaseUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            Log.d("ONBOARD : ", "onAuthStateChanged: SIGNED OUT")
        }

        setContentView(R.layout.activity_onboarding)

        button_onboard_victim_signup.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        button_onboard_rescuer_signup.setOnClickListener{
            val intent = Intent(this, SignInRescuerActivity::class.java)
            startActivity(intent)
        }
    }
}