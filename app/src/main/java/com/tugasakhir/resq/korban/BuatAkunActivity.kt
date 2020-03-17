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
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.activity_korban_buatakun.*
import java.util.concurrent.TimeUnit

const val EXTRA_NAME = "com.tugasakhir.resq.korban.NAME"
const val EXTRA_PHONE = "com.tugasakhir.resq.korban.PHONE"

class BuatAkunActivity : AppCompatActivity() {

    val TAG = "BUAT AKUN"

    private lateinit var actionBar : ActionBar
    lateinit var mCallbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_korban_buatakun)

        actionBar = this.supportActionBar!!
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_logo_round)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "BUAT AKUN"
        actionBar.elevation = 0F

        button_signup_tosignin.setOnClickListener{
            val intent = Intent(this, MasukActivity::class.java)
            startActivity(intent)
        }

        button_signup_continue.setOnClickListener{
            registerUser()
        }

    }

    private fun registerUser() {
        val name = edittext_signup_name.text.toString().trim()
        val phone = edittext_signup_phone.text.toString().trim()

        if (name.isEmpty()) {
            edittext_signup_name.error = getString(R.string.field_is_empty)
            edittext_signup_name.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            edittext_signup_phone.error = getString(R.string.field_is_empty)
            edittext_signup_phone.requestFocus()
            return
        }

        verificationCallbacks()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone,
            60,
            TimeUnit.SECONDS,
            this,
            mCallbacks)

        Log.wtf("NAME: ", name)
        Log.wtf("PHONE: ", phone)

        senddata(name, phone)

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
                Log.d(TAG, "onVerificationCompleted:$credential")            }

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

//            override fun onCodeSent(verificationId: String, p1: token.ForceResendingToken) {
//                // The SMS verification code has been sent to the provided phone number, we
//                // now need to ask the user to enter the code and then construct a credential
//                // by combining the code with a verification ID.
//                Log.d(TAG, "onCodeSent:$verificationId")
//
//                // Save verification ID and resending token so we can use them later
//                storedVerificationId = verificationId
//                resendToken = token
//            }
        }
    }

    fun senddata(name: String, phone: String) {
        val intent = Intent(this, OTPActivity::class.java).apply {
            putExtra(EXTRA_NAME, name)
            putExtra(EXTRA_PHONE, phone)
        }
        startActivity(intent)
    }

}