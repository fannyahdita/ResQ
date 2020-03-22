package com.tugasakhir.resq.korban

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
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


    private lateinit var actionBar: ActionBar
    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var mAuth: FirebaseAuth
    var verificationId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_korban_otp)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.otp_actionbar)
        actionBar.elevation = 0F

        val phone = intent.getStringExtra(EXTRA_PHONE)

        mAuth = FirebaseAuth.getInstance()

        verify(phone)

        Log.wtf("OTP PHONE: ", phone)

        button_sendotp.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (currentFocus != null) inputMethodManager.hideSoftInputFromWindow(
                currentFocus!!.applicationWindowToken, 0
            )
            authenticate(phone)
        }
        button_sendagain.setOnClickListener {
            progressbar_otp.visibility = View.VISIBLE
            verify(phone)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun verify(phone: String) {
        Log.d("PHONE AUTH OTP", "PHONE VERIFICATION")
        verificationCallbacks()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone,
            60,
            TimeUnit.SECONDS,
            this,
            mCallbacks
        )

        progressbar_otp.visibility = View.GONE

    }

    private fun verificationCallbacks() {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("PHONE AUTH OTP", "onVerificationCompleted:$credential")
                Toast.makeText(this@OTPActivity, "Kode verifikasi sudah terkirim", Toast.LENGTH_SHORT).show()
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.w("PHONE AUTH OTP", "onVerificationFailed", p0)
                Toast.makeText(this@OTPActivity, "Gagal mengirim kode verifikasi. Pastikan nomor yang dimasukkan benar.", Toast.LENGTH_SHORT).show()

                if (p0 is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (p0 is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
            }

            override fun onCodeSent(
                verfication: String,
                p1: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verfication, p1)
                verificationId = verfication
                Log.d("PHONE AUTH OTP", "onCodeSent$verificationId")

            }

        }
    }

    private fun authenticate(phone: String) {
        val code = edittext_otp1.text.toString().trim()

        try {
            val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code)
            progressbar_otp.visibility = View.VISIBLE
            button_sendotp.isClickable = false
            button_sendotp.setBackgroundResource(R.drawable.shape_filled_button_clicked)
            signInWithPhoneAuthCredential(credential, phone)

        } catch (e: Exception) {
            val builder = AlertDialog.Builder(this@OTPActivity)
            builder.setTitle("Verifikasi Gagal")
            builder.setMessage("Pastikan kode verifikasi yang Anda masukkan benar")
            builder.setNeutralButton("Oke"){_,_ ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, phone: String) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("PHONE AUTH OTP", "signInWithCredential:success")
                    Log.d(
                        "PHONE AUTH OTP",
                        "CREATED AT 1 " + (task.result?.user?.metadata?.creationTimestamp).toString()
                    )
                    Log.d(
                        "PHONE AUTH OTP",
                        "CREATED AT 2 " + (task.result?.user?.metadata?.lastSignInTimestamp).toString()
                    )

                    if (task.result?.user?.metadata?.creationTimestamp != task.result?.user?.metadata?.lastSignInTimestamp) {
                        progressbar_otp.visibility = View.GONE
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                        Log.d("PHONE AUTH OTP", "AKUN LAMA INI YAAA")
                    } else {
                        progressbar_otp.visibility = View.GONE
                        val intent = Intent(this, UserNamaActivity::class.java)
                        intent.putExtra(EXTRA_PHONE, phone)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                        Log.d("PHONE AUTH OTP", "AKUN BARU YAAA")
                    }

                    val user = task.result?.user
                    // ...
                } else {
                    // false OTP input
                    progressbar_otp.visibility = View.GONE
                    button_sendotp.isClickable = true
                    button_sendotp.setBackgroundResource(R.drawable.shape_filled_button)
                    Log.w("PHONE AUTH OTP", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }


}