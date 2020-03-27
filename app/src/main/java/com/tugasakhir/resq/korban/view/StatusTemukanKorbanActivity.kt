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

class StatusTemukanKorbanActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var mHandler: Handler

    var accept: Boolean = false
    var running: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_main_status_korban)

        val statusSent = StatusSentFragment.newInstance()
        openFragment(statusSent)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        updateStatus()
        mHandler = Handler()

        runnableCode.run()

    }

    val runnableCode = object: Runnable {
        override fun run() {
//            checkStatus()
            updateFragment(accept, running)

            Toast.makeText(this@StatusTemukanKorbanActivity, "Jalanin Runnable", Toast.LENGTH_LONG).show()
            if (!accept) {
                accept = true
            } else {
                running = true
            }

            mHandler.postDelayed(this, 10000)

        }
    }

    private fun updateStatus() {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("AkunKorban/$user").child("askingHelp").setValue(true)

    }

    private fun checkStatus() {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("KorbanTertolong/$user")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val korbanTertolong = p0.getValue(KorbanTertolong::class.java)
                    updateFragment(korbanTertolong!!.isAccepted, korbanTertolong!!.isOntheWay)
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("DatabaseReference : ", "user with id $user is not exist")
                    Toast.makeText(this@StatusTemukanKorbanActivity, p0.message, Toast.LENGTH_SHORT).show()
                }
            })

    }

    private fun updateFragment(isAccepted: Boolean, isRunning: Boolean) {
        if (isRunning) {
            val runningFragment = StatusRunningFragment.newInstance()
            openFragment(runningFragment)
        } else if (isAccepted) {
            val acceptedFragment = StatusAcceptedFragment.newInstance()
            openFragment(acceptedFragment)
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
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mHandler.removeCallbacks(runnableCode)

        val user = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("AkunKorban/$user").child("askingHelp").setValue(false)

    }

}