package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.activity_temukansayaform_korban.*

class FormTemukanKorbanActivity : AppCompatActivity() {

    private lateinit var actionBar : ActionBar
    var lansia_displayInt : Int = 0
    var anak_displayInt : Int = 0
    var dewasa_displayInt : Int = 0
    var status: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temukansayaform_korban)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        button_send.setOnClickListener {
            val intent = Intent(this, Status1TemukanKorbanActivity::class.java)
            startActivity(intent)
        }

        decrease_lansia.setOnClickListener {
            if (lansia_displayInt != 0) {
                lansia_displayInt--
            }
            status = 1
            displayNumber(lansia_displayInt)
        }

        decrease_anak.setOnClickListener {
            if (anak_displayInt != 0) {
                anak_displayInt--
            }
            status = 3
            displayNumber(anak_displayInt)
        }

        decrease_dewasa.setOnClickListener {
            if (dewasa_displayInt != 0) {
                dewasa_displayInt--
            }
            status = 2
            displayNumber(dewasa_displayInt)
        }

        increase_lansia.setOnClickListener {
            lansia_displayInt++
            status = 1
            displayNumber(lansia_displayInt)
        }

        increase_anak.setOnClickListener {
            anak_displayInt++
            status = 3
            displayNumber(anak_displayInt)
        }

        increase_dewasa.setOnClickListener {
            dewasa_displayInt++
            status = 2
            displayNumber(dewasa_displayInt)
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

    private fun displayNumber(number: Int) {
        if (status == 3) {
            integer_number_anak.setText("" + number)
        } else if (status == 2) {
            integer_number_dewasa.setText("" + number)
        } else {
            integer_number_lansia.setText("" + number)
        }
    }

}