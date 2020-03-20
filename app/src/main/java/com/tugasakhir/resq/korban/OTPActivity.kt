package com.tugasakhir.resq.korban

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.Korban
import kotlinx.android.synthetic.main.activity_korban_otp.*
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    val TAG = "PHONE AUTH OTP"

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

        val phone = intent.getStringExtra(EXTRA_PHONE)

        mAuth = FirebaseAuth.getInstance()

        verify(phone)

        Log.wtf("OTP PHONE: ", phone)

        button_sendotp.setOnClickListener{
            authenticate(phone)
        }
        button_sendagain.setOnClickListener{
            verify(phone)
        }

    }

    private fun verify(phone: String) {
        Log.d(TAG, "VERIFIKASI PHONE")
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
                Log.d(TAG, "onVerificationCompleted:$credential")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", p0)

                if (p0 is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (p0 is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
            }

            override fun onCodeSent(verfication: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verfication, p1)
                verificationId = verfication.toString()
                Log.d(TAG, "onCodeSent" + verificationId)

            }

        }
    }

    private fun authenticate(phone: String) {
        val code = edittext_otp1.text.toString().trim()

        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code)

        signInWithPhoneAuthCredential(credential, phone)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, phone: String) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    Log.d(TAG, "CREATED AT 1 " + (task.result?.user?.metadata?.creationTimestamp).toString())
                    Log.d(TAG, "CREATED AT 2 " + (task.result?.user?.metadata?.lastSignInTimestamp).toString())

                    if (task.result?.user?.metadata?.creationTimestamp != task.result?.user?.metadata?.lastSignInTimestamp) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                        Log.d(TAG, "AKUN LAMA INI YAAA")
                    } else {
                        val intent = Intent(this, UserNamaActivity::class.java)
                        intent.putExtra(EXTRA_PHONE, phone)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                        Log.d(TAG, "AKUN BARU YAAA")
                    }

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



}