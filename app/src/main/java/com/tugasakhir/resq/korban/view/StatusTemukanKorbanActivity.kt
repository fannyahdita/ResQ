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
import com.tugasakhir.resq.rescuer.model.Rescuer


class StatusTemukanKorbanActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var mHandler: Handler
    private lateinit var rescuer: Rescuer

    var rescuerName: String = ""

    var isAlreadyFinished: Boolean = false

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

    private val runnableCode = object: Runnable {
        override fun run() {
            getIdKorban()

            Toast.makeText(this@StatusTemukanKorbanActivity, "Jalanin Runnable", Toast.LENGTH_LONG).show()
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

                        if (userId == it.child("idInfoKorban").value.toString() &&
                            it.child("finished").value!!.toString() == "false") {

                            val idKorbanTertolong = it.key.toString()
                            val idRescuer = it.child("idRescuer").value.toString()

                            getRescuerData(idRescuer)
                            updateFragment(
                                it.child("accepted").value!!.equals(true),
                                it.child("onTheWay").value!!.equals(true),
                                it.child("rescuerArrived").value!!.equals(true),
                                userId, idKorbanTertolong, idRescuer
                            )
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })

    }

    private fun updateFragment(isAccepted: Boolean, isRunning: Boolean, isRescuerArrived: Boolean, idInfoKorban: String, idKorbanTertolong: String, idRescuer: String) {
        if (isRescuerArrived && !isAlreadyFinished && rescuerName != "") {
            isAlreadyFinished = true
            val finishFragment = StatusFinishedFragment.newInstance(rescuer, idInfoKorban, idKorbanTertolong)
            openFragment(finishFragment)
        } else if (isRunning && rescuerName != "") {
            val runningFragment = StatusRunningFragment.newInstance(rescuer)
            openFragment(runningFragment)
        } else if (isAccepted && rescuerName != "") {
            val acceptedFragment = StatusAcceptedFragment.newInstance(idInfoKorban, idKorbanTertolong, rescuer)
            openFragment(acceptedFragment)
        }
    }

    private fun getRescuerData(idRescuer: String?) {
        FirebaseDatabase.getInstance().reference.child("Rescuers/$idRescuer")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    rescuer = p0.getValue(Rescuer::class.java)!!
                    rescuerName = p0.child("name").value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })
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