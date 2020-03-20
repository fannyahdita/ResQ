package com.tugasakhir.resq.korban

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_logo_round)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "BUAT AKUN"
        actionBar.elevation = 0F

        button_signup_continue.setOnClickListener{
            registerUser()
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