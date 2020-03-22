package com.tugasakhir.resq

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.rescuer.view.HomeFragment
import com.tugasakhir.resq.rescuer.view.PoskoRescuerFragment
import com.tugasakhir.resq.rescuer.view.ProfileRescuerFragment
import com.tugasakhir.resq.rescuer.view.TemukanSayaRescuerFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private var isKorban: Boolean = false
    private var doubleBackToExitPressedOnce = false

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            item.setCheckable(true)
            when (item.itemId) {
                R.id.navigation_beranda -> {
                    actionBar.title = "Res-Q"
                    val homeFragment = HomeFragment.newInstance()
                    openFragment(homeFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_posko -> {
                    actionBar.title = "Posko"
                    if (isKorban) {
                        val poskoKorban = Fragment_Posko_Korban.newInstance()
                        openFragment(poskoKorban)
                    } else {
                        val poskoRescuer = PoskoRescuerFragment.newInstance()
                        openFragment(poskoRescuer)
                    }
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
                    val profileRescuerFragemnt = ProfileRescuerFragment.newInstance()
                    openFragment(profileRescuerFragemnt)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("Korban/$user")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    isKorban = p0.exists()
                    Log.d("DatabaseReference : ", p0.toString())
                    Log.d("DatabaseReference : ", "korban $isKorban")
                    showTemukanSaya(isKorban)
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("DatabaseReference : ", "user with id $user is ot exist")
                }
            })

        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        actionBar = this.supportActionBar!!
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_logo_round)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "RES-Q"
        actionBar.elevation = 0F

        navigation_temukan.setOnClickListener {
            actionBar.title = "Temukan Saya"
            disableNavigation(navigationView)
            val temukanSayaFragment = Fragment_TemukanSaya_Korban.newInstance()
            openFragment(temukanSayaFragment)
            showTemukanSaya(isKorban)
        }

    }

    private fun showTemukanSaya(isKorban: Boolean) {
        if (isKorban) {
            val temukanSayaKorban = Fragment_TemukanSaya_Korban.newInstance()
            openFragment(temukanSayaKorban)
        } else {
            val temukanSayaRescuer = TemukanSayaRescuerFragment.newInstance()
            openFragment(temukanSayaRescuer)
        }
    }

    private fun disableNavigation(bottomNavigation: BottomNavigationView) {
        var menuBar : Menu = bottomNavigation.menu
        for (i in 0 until menuBar.size()) {
            if (i != 2) {
                menuBar.getItem(i).setCheckable(false)
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }


}