package com.tugasakhir.resq.korban

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
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
        actionBar.title = "BUAT AKUN"
        actionBar.elevation = 0F

        button_signup_continue.setOnClickListener{
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if(currentFocus != null) inputMethodManager.hideSoftInputFromWindow(
                currentFocus!!.applicationWindowToken, 0
            )
            registerUser()
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

    private fun registerUser() {
        val phone = edittext_signup_phone.text.toString().trim()

        if (phone.isEmpty()) {
            edittext_signup_phone.error = getString(R.string.field_is_empty)
            edittext_signup_phone.requestFocus()
            return
        }

        Log.wtf("PHONE: ", phone)

        senddata(phone)

    }


    fun senddata(phone: String) {
        val intent = Intent(this, OTPActivity::class.java).apply {
            putExtra(EXTRA_PHONE, phone)
        }
        startActivity(intent)
    }

}