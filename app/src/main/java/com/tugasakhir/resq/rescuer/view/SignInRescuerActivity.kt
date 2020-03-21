package com.tugasakhir.resq.rescuer.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.OnboardingActivity
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.activity_rescuer_signin.*

class SignInRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rescuer_signin)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "BUAT AKUN"
        actionBar.elevation = 0F

        firebaseAuth = FirebaseAuth.getInstance()

        button_signin_tosignup.setOnClickListener {
            val intent = Intent(this, SignUpRescuerActivity::class.java)
            startActivity(intent)
            finish()
        }

        button_signin_finish.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (currentFocus != null) inputMethodManager.hideSoftInputFromWindow(
                currentFocus!!.applicationWindowToken, 0
            )
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

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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
        button_signin_finish.isClickable = false
        button_signin_finish.setBackgroundResource(R.drawable.shape_filled_button_clicked)
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressbar_signin.visibility = View.GONE
                    Log.d("TAG", "signInWithEmail:success")
                    Toast.makeText(
                        this@SignInRescuerActivity, getString(R.string.toast_logged_in),
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                    val intent = Intent(this@SignInRescuerActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)

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