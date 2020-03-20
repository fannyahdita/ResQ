package com.tugasakhir.resq.korban

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.Korban
import kotlinx.android.synthetic.main.activity_korban_nama.*

class UserNamaActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_korban_nama)

        actionBar = this.supportActionBar!!
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_logo_round)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "MASUKKAN NAMA"
        actionBar.elevation = 0F

        val phone = intent.getStringExtra(EXTRA_PHONE)

        button_signup_continue.setOnClickListener{
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if(currentFocus != null) inputMethodManager.hideSoftInputFromWindow(
                currentFocus!!.applicationWindowToken, 0
            )

            val name = edittext_signup_name.text.toString().trim()
            Log.wtf("NAMANYA ADALAH  : ", name)
            korbanDatabase(name, phone)
        }

    }

    private fun korbanDatabase(name: String, phone: String) {
        val korban = Korban(name, phone)
        FirebaseDatabase.getInstance().getReference("Korban")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .setValue(korban).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Toast.makeText(this, getString(R.string.toast_account_created), Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }

    }
}