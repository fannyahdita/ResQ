package com.tugasakhir.resq.korban.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.base.view.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.base.view.HomeFragment
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

        textview_number_phone_otp.text = phone

        mAuth = FirebaseAuth.getInstance()

        verify(phone)

        timer(60000,1000).start()

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
            button_sendagain.isEnabled = false
            timer(60000,1000).start()
            verify(phone)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.dialog_box_batalkan)
                builder.setMessage(R.string.dialog_box_batalkan_penjelasan)
                builder.setNegativeButton(R.string.alert_tetap_batalkan){_,_ ->
                    onBackPressed()
                    finish()
                }

                builder.setPositiveButton(R.string.alert_jangan_batalkan){_,_ ->

                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
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
//                Toast.makeText(this@OTPActivity, "Kode verifikasi sudah terkirim", Toast.LENGTH_SHORT).show()
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.w("PHONE AUTH OTP", "onVerificationFailed", p0)
//                Toast.makeText(this@OTPActivity, "Gagal mengirim kode verifikasi. Pastikan nomor yang dimasukkan benar.", Toast.LENGTH_SHORT).show()

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

                    val user = task.result?.user?.uid

                    FirebaseDatabase.getInstance().reference.child("AkunKorban/$user")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.exists()) {
                                    progressbar_otp.visibility = View.GONE
                                    openActivity(false, phone)
                                } else {
                                    progressbar_otp.visibility = View.GONE
                                    openActivity(true, phone)
                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                Log.d("DatabaseReference : ", "user with id $user is not exist")
                            }
                        })

                } else {
                    // false OTP input
                    progressbar_otp.visibility = View.GONE
                    button_sendotp.isClickable = true
                    button_sendotp.setBackgroundResource(R.drawable.shape_filled_button)
                    val builder = AlertDialog.Builder(this@OTPActivity)
                    builder.setTitle("Verifikasi Gagal")
                    builder.setMessage("Pastikan kode verifikasi yang Anda masukkan benar")
                    builder.setNeutralButton("Oke"){_,_ ->

                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                    Log.w("PHONE AUTH OTP", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    private fun openActivity(isNew: Boolean, phone: String) {
        if (isNew) {
            val intent = Intent(this, UserNamaActivity::class.java)
            intent.putExtra(EXTRA_PHONE, phone)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

            Log.d("PHONE AUTH OTP", "AKUN BARU YAAA")
        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

            Log.d("PHONE AUTH OTP", "AKUN LAMA INI YAAA")
        }
    }

    private fun timer(millisInFuture:Long,countDownInterval:Long): CountDownTimer {
        return object: CountDownTimer(millisInFuture,countDownInterval){
            override fun onTick(millisUntilFinished: Long){
                count_down_timer.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                button_sendagain.isEnabled = true
            }
        }
    }


}
