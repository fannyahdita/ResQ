package com.tugasakhir.resq

import android.content.Intent
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
import com.tugasakhir.resq.korban.view.PoskoKorbanFragment
import com.tugasakhir.resq.korban.view.StatusTemukanKorbanActivity
import com.tugasakhir.resq.korban.view.TemukanSayaActivity
import com.tugasakhir.resq.rescuer.view.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private var isKorban: Boolean = false
    private var isAskingHelp: Boolean = false
    private var isHelping: Boolean = false
    private var doubleBackToExitPressedOnce = false

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            item.isCheckable = true
            when (item.itemId) {
                R.id.navigation_beranda -> {
                    actionBar.title = getString(R.string.app_name_actionbar)
                    val homeFragment = HomeFragment.newInstance()
                    openFragment(homeFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_posko -> {
                    actionBar.title = getString(R.string.posko_actionbar)
                    if (isKorban) {
                        val poskoKorban = PoskoKorbanFragment.newInstance()
                        openFragment(poskoKorban)
                    } else {
                        val poskoRescuer = PoskoRescuerFragment.newInstance()
                        openFragment(poskoRescuer)
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_kontak -> {
                    actionBar.title = getString(R.string.contact_actionbar)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_akun -> {
                    actionBar.title = getString(R.string.profile_actionbar)
                    val profileRescuerFragment = ProfileRescuerFragment.newInstance(isKorban)
                    openFragment(profileRescuerFragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment.newInstance()
        openFragment(homeFragment)

        val user = FirebaseAuth.getInstance().currentUser?.uid

        setIsHelping(user!!)

        FirebaseDatabase.getInstance().reference.child("AkunKorban/$user")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    isKorban = p0.exists()
                    if (isKorban) {
                        isAskingHelp = p0.child("askingHelp").value!!.equals("true")
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("DatabaseReference : ", "user with id $user is not exist")
                    Toast.makeText(this@MainActivity, p0.message, Toast.LENGTH_SHORT).show()
                }
            })


        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        actionBar = this.supportActionBar!!
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_logo_round)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.app_name_actionbar)
        actionBar.elevation = 0F

        navigation_temukan.setOnClickListener {
            Log.d("isHelpingDalamOnclick ", isHelping.toString())
            actionBar.title = getString(R.string.temukansaya_actionbar)
            disableNavigation(navigationView)
            showTemukanSaya(isKorban)
        }

    }

    private fun showTemukanSaya(isKorban: Boolean) {
        if (isKorban) {
            if (isAskingHelp) {
                val intent = Intent(this, StatusTemukanKorbanActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, TemukanSayaActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            if (isHelping) {
                val intent = Intent(this, HelpVictimActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, TemukanSayaRescuerActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun disableNavigation(bottomNavigation: BottomNavigationView) {
        val menuBar: Menu = bottomNavigation.menu
        for (i in 0 until menuBar.size()) {
            if (i != 2) {
                menuBar.getItem(i).isCheckable = false
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
        Toast.makeText(this, getString(R.string.toast_exit), Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun setIsHelping(rescuerId: String) {
        Log.d("isHelping user", rescuerId)
        FirebaseDatabase.getInstance().getReference("Rescuers")
            .child(rescuerId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        isHelping = p0.child("helping").value.toString() == "true"
                        Log.d("isHelping 1", isHelping.toString())
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("IsHelpingRescuerError: ", p0.message)
                }
            })
    }
}