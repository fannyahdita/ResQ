package com.tugasakhir.resq.rescuer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.OnboardingActivity
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.activity_rescuer_signin.*

class SignInRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rescuer_signin)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "BUAT AKUN"
        actionBar.elevation = 0F

        firebaseAuth = FirebaseAuth.getInstance()

        authStateListener = FirebaseAuth.AuthStateListener {
            val user : FirebaseUser? = it.currentUser

            if(user != null) {
                //user is signed in
            } else {
                //user is signed out
            }
        }

        button_signin_tosignup.setOnClickListener {
            val intent = Intent(this, SignUpRescuerActivity::class.java)
            startActivity(intent)
            finish()
        }

        button_signin_finish.setOnClickListener {
            isValidUser()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isValidUser() {
        val email = edittext_signin_email.text.toString().trim()
        val password = edittext_signin_password.text.toString().trim()

        if (email.isEmpty()) {
            edittext_signin_email.error = getString(R.string.field_is_empty)
            edittext_signin_email.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edittext_signin_email.error = getString(R.string.email_is_not_valid)
            edittext_signin_email.requestFocus()
            return
        }

        if (password.isEmpty()) {
            edittext_signin_password.error = getString(R.string.field_is_empty)
            edittext_signin_password.requestFocus()
            return
        }

        Log.wtf("SIGN IN: ", "$email $password")
        loggingInUser(email, password)
    }

    private fun loggingInUser(email: String, password: String) {
        progressbar_signin.visibility = View.VISIBLE
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressbar_signin.visibility = View.GONE
                    Log.d("TAG", "signInWithEmail:success")
                    Toast.makeText(
                        this@SignInRescuerActivity, getString(R.string.toast_logged_in),
                        Toast.LENGTH_LONG
                    ).show()

                    val user = firebaseAuth.currentUser
                    finish()
                    val intent = Intent(this@SignInRescuerActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    progressbar_signin.visibility = View.GONE
                    Toast.makeText(
                        this@SignInRescuerActivity, task.exception?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}