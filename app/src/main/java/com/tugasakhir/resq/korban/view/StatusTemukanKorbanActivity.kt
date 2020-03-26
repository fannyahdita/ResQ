package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.KorbanTertolong
import com.tugasakhir.resq.rescuer.view.HomeFragment
import com.tugasakhir.resq.rescuer.view.PoskoRescuerFragment

class Status1TemukanKorbanActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var mHandler: Handler

    var accept: Boolean = false
    var running: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment.newInstance()
        openFragment(homeFragment)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        mHandler = Handler()

        runnableCode.run()

    }

    val runnableCode = object: Runnable {
        override fun run() {
//            checkStatus()
            updateFragment(accept, running)

            Toast.makeText(this@Status1TemukanKorbanActivity, "Jalanin Runnable", Toast.LENGTH_LONG).show()
            if (!running) {
                running = true
            } else {
                accept = true
            }

            mHandler.postDelayed(this, 10000)

        }
    }

    private fun checkStatus() {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("KorbanTertolong/$user")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val korbanTertolong = p0.getValue(KorbanTertolong::class.java)
                    updateFragment(korbanTertolong!!.isAccepted, korbanTertolong!!.isRunning)
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("DatabaseReference : ", "user with id $user is not exist")
                    Toast.makeText(this@Status1TemukanKorbanActivity, p0.message, Toast.LENGTH_SHORT).show()
                }
            })

    }

    private fun updateFragment(isAccepted: Boolean, isRunning: Boolean) {
        if (isAccepted) {
            val poskoKorban = PoskoKorbanFragment.newInstance()
            openFragment(poskoKorban)
        } else if (isRunning) {
            val poskoRescuer = PoskoRescuerFragment.newInstance()
            openFragment(poskoRescuer)
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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

}