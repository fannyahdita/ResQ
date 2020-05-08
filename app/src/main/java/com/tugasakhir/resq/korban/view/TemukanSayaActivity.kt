package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.tugasakhir.resq.base.view.MainActivity
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.activity_temukansaya_korban.*

class TemukanSayaActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temukansaya_korban)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        val lat = intent.getStringExtra(EXTRA_LAT)
        val long = intent.getStringExtra(EXTRA_LONG)

        temukansaya_button.setOnClickListener {
            val intent = Intent(this, FormTemukanKorbanActivity::class.java)
            intent.putExtra(EXTRA_LAT, lat)
            intent.putExtra(EXTRA_LONG, long)
            startActivity(intent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}