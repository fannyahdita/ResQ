package com.tugasakhir.resq

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tugasakhir.resq.rescuer.view.ProfileRescuerFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var actionBar : ActionBar

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_beranda -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_posko -> {
                actionBar.title = "Posko"

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_temukan -> {
                actionBar.title = "Temukan saya"

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_kontak -> {
                actionBar.title = "Kontak"

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_akun -> {

                actionBar.title = "Profil"
                val profileRescuerFragemnt =   ProfileRescuerFragment.newInstance()
                openFragment(profileRescuerFragemnt)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        actionBar = this.supportActionBar!!
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_logo_round)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "RES-Q"
        actionBar.elevation = 0F

        navigation_temukan.setOnClickListener {
            actionBar.title = "Temukan Saya"
//            val temukanSayaFragment = Fragment_TemukanSaya_Korban.newInstance()
//            openFragment(temukanSayaFragment)
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }



}
