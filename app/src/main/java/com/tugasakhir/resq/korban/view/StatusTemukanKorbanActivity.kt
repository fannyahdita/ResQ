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

class StatusTemukanKorbanActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var mHandler: Handler

    var accept: Boolean = false
    var running: Boolean = false
//    var finish: Boolean = false
    var idInfoKorban: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_main_status_korban)

        val statusSent = StatusSentFragment.newInstance()
        openFragment(statusSent)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

//        if (intent.getStringExtra(EXTRA_PREV_ACTIVITY) == "Form") {
//            userId = intent.getStringExtra(EXTRA_ID_INFOKORBAN)
//        }
        getIdKorban()

        updateStatus()
        mHandler = Handler()

        runnableCode.run()

    }

    val runnableCode = object: Runnable {
        override fun run() {
            checkStatus(idInfoKorban)
//            updateFragment(accept, running, finish)
//            getIdKorban()

            Toast.makeText(this@StatusTemukanKorbanActivity, "Jalanin Runnable", Toast.LENGTH_LONG).show()
//            if (!accept) {
//                accept = true
//            } else if (!running) {
//                running = true
//            } else if (!finish) {
//                finish = true
//            }

            mHandler.postDelayed(this, 10000)

        }
    }

    private fun updateStatus() {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("AkunKorban/$user").child("askingHelp").setValue(true)

    }

    private fun getIdKorban() {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().getReference("InfoKorban")
            .addListenerForSingleValueEvent(object  : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val children = p0.children
                    children.forEach {
                        Log.d("ID KORBAN 1 : ", user)
                        Log.d("ID KORBAN 2 : ", it.child("idKorban").value.toString())

                        if (user == it.child("idKorban").value.toString()) {
                            Toast.makeText(this@StatusTemukanKorbanActivity, "Sudah dapet idInfoKorban : " + it.child("idKorban").value.toString(), Toast.LENGTH_LONG).show()
                            checkStatus(it.key.toString())
                        }

                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)                }
            })

    }


    private fun checkStatus(userId: String) {
        FirebaseDatabase.getInstance().getReference("KorbanTertolong")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val children = p0.children
                    children.forEach {
                        Log.d("ID KORBAN INFO 1 : ", userId)
                        Log.d("ID KORBAN INFO 2 : ", it.child("idInfoKorban").value.toString())
                        Toast.makeText(this@StatusTemukanKorbanActivity, "Masuk chcek KorbanTertolong" + it.child("idInfoKorban").value.toString(), Toast.LENGTH_LONG).show()

                        if (userId == it.child("idInfoKorban").value.toString() &&
                            it.child("finished").value!!.toString() == "false") {
                            idInfoKorban = userId
                            Toast.makeText(this@StatusTemukanKorbanActivity, "Mau update fragment : " + it.child("idInfoKorban").value.toString(), Toast.LENGTH_LONG).show()
                            updateFragment(
                                it.child("accepted").value!!.equals(true),
                                it.child("onTheWay").value!!.equals(true),
                                it.child("finished").value!!.equals(true)
                            )
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })

    }

    private fun updateFragment(isAccepted: Boolean, isRunning: Boolean, isFinished: Boolean) {
        if (isFinished) {
            val user = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseDatabase.getInstance().reference.child("AkunKorban/$user").child("askingHelp").setValue(false)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else if (isRunning) {
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

    }

}