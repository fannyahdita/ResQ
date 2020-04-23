package com.tugasakhir.resq.rescuer.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.activity_thank_you_rescuer.*

class ThankYouRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private var currLat : String? = ""
    private var currLong : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you_rescuer)

        actionBar = this.supportActionBar!!
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_logo_round)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        currLat = intent.getStringExtra("EXTRA_LAT")?.toString()
        currLong = intent.getStringExtra("EXTRA_LONG")?.toString()

        button_to_temukan_saya.setOnClickListener {
            val intent = Intent(this, TemukanSayaRescuerActivity::class.java)
            intent.putExtra("EXTRA_LAT", currLat)
            intent.putExtra("EXTRA_LONG", currLong)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, TemukanSayaRescuerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}