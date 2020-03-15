package com.tugasakhir.resq

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tugasakhir.resq.rescuer.SignInRescuerActivity
import com.tugasakhir.resq.rescuer.SignUpRescuerActivity
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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