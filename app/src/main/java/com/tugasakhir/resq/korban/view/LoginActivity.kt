package com.tugasakhir.resq.korban.view

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
import com.tugasakhir.resq.OnboardingActivity
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.activity_korban_buatakun.*

const val EXTRA_PHONE = "com.tugasakhir.resq.korban.PHONE"

class BuatAkunActivity : AppCompatActivity() {

    private lateinit var actionBar : ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_korban_buatakun)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.signin_actionbar)
        actionBar.elevation = 0F

        button_signup_continue.isClickable = true
        button_signup_continue.setBackgroundResource(R.drawable.shape_filled_button)
        button_signup_continue.setOnClickListener{
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if(currentFocus != null) inputMethodManager.hideSoftInputFromWindow(
                currentFocus!!.applicationWindowToken, 0
            )
            registerUser()
        }

    }

    override fun onResume() {
        button_signup_continue.isClickable = true
        button_signup_continue.setBackgroundResource(R.drawable.shape_filled_button)
        super.onResume()
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

    private fun registerUser() {
        val phone = "+62" + edittext_signup_phone.text.toString().trim()

        if (phone.equals("+62")) {
            edittext_signup_phone.error = getString(R.string.field_is_empty)
            edittext_signup_phone.requestFocus()
            return
        } else if (!Patterns.PHONE.matcher(phone).matches() || phone.length < 12 || phone.length > 14) {
            edittext_signup_phone.error = "Nomor yang anda masukan tidak valid"
            edittext_signup_phone.requestFocus()
            Toast.makeText(this, "Nomor telepon tidak valid", Toast.LENGTH_LONG).show()
            return
        }

        Log.wtf("PHONE: ", phone)

        progressbar_phone.visibility = View.VISIBLE
        button_signup_continue.isClickable = false
        button_signup_continue.setBackgroundResource(R.drawable.shape_filled_button_clicked)

        senddata(phone)

    }


    fun senddata(phone: String) {
        progressbar_phone.visibility = View.GONE
        val intent = Intent(this, OTPActivity::class.java).apply {
            putExtra(EXTRA_PHONE, phone)
        }
        startActivity(intent)
    }

}