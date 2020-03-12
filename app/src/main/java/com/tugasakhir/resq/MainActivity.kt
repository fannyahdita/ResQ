package com.tugasakhir.resq

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var toolbar: ActionBar
    private lateinit var actionBar : ActionBar

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_beranda -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_posko -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_temukan -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_kontak -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_akun -> {

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = supportActionBar!!
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        actionBar = this.supportActionBar!!
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_logo_round)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "RES-Q"
        actionBar.elevation = 0F
    }



}
