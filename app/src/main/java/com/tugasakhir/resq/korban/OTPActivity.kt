package com.tugasakhir.resq.korban

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.activity_korban_otp.*
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    val TAG = "OTP"

    private lateinit var actionBar : ActionBar
    lateinit var mCallbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var mAuth : FirebaseAuth
    var verificationId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_korban_otp)

        actionBar = this.supportActionBar!!
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_logo_round)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "OTP"
        actionBar.elevation = 0F

        val name = intent.getStringExtra(EXTRA_NAME)
        val phone = intent.getStringExtra(EXTRA_PHONE)

        mAuth = FirebaseAuth.getInstance()

        verify(phone)

        Log.wtf("OTP NAME: ", name)
        Log.wtf("OTP PHONE: ", phone)

        button_sendotp.setOnClickListener{
            authenticate()
        }

    }

    private fun verify(phone: String) {
        verificationCallbacks()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone,
            60,
            TimeUnit.SECONDS,
            this,
            mCallbacks)

    }

    private fun verificationCallbacks() {
        mCallbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", p0)

                if (p0 is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (p0 is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(verfication: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verfication, p1)
                verificationId = verfication.toString()
            }

        }
    }

    private fun authenticate() {
        val code = edittext_otp1.text.toString().trim()

        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code)

        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    // ...
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    private fun checkUser() {

    }




}