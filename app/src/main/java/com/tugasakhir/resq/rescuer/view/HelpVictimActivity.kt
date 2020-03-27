package com.tugasakhir.resq.rescuer.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.activity_help_victim.*

class HelpVictimActivity: AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private var victimInfoId : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_victim)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

//        val bundle = intent.extras
//        victimInfoId = bundle?.getString("victimInfoId")
//
//        textview_name_victim_help.text = victimInfoId

    }
}