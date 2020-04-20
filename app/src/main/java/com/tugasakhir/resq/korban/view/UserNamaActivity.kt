package com.tugasakhir.resq.korban.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.AkunKorban
import kotlinx.android.synthetic.main.activity_korban_nama.*

class UserNamaActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_korban_nama)

        actionBar = this.supportActionBar!!
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_logo_round)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.victim_name_actionbar)
        actionBar.elevation = 0F

        val phone = intent.getStringExtra(EXTRA_PHONE)

        button_signup_continue.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (currentFocus != null) inputMethodManager.hideSoftInputFromWindow(
                currentFocus!!.applicationWindowToken, 0
            )
            val name = edittext_signup_name.text.toString().trim()
            Log.wtf("NAMANYA ADALAH  : ", name)
            korbanDatabase(name, phone!!)
        }

        textview_max_char_name.text = getString(R.string.max_char_50,0)
        edittext_signup_name.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) { }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textview_max_char_name.text = getString(R.string.max_char_50, p0?.length)
            }
        })

    }

    private fun korbanDatabase(name: String, phone: String) {
        progressbar_name.visibility = View.VISIBLE
        button_signup_continue.isClickable = false
        button_signup_continue.setBackgroundResource(R.drawable.shape_filled_button_clicked)
        val korban = AkunKorban(name, phone, "", false)
        FirebaseDatabase.getInstance().getReference("AkunKorban")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .setValue(korban).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Toast.makeText(
                        this,
                        getString(R.string.toast_account_created),
                        Toast.LENGTH_SHORT
                    ).show()
                    progressbar_name.visibility = View.GONE
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                } else {
                    progressbar_name.visibility = View.GONE
                    button_signup_continue.isClickable = true
                    button_signup_continue.setBackgroundResource(R.drawable.shape_filled_button)
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }

    }
}