package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.model.Rescuer
import kotlinx.android.synthetic.main.fragment_main_status_korban.*


class StatusTemukanKorbanActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var rescuer: Rescuer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_main_status_korban)
        progressbar_name.visibility = View.VISIBLE

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        updateStatus()
        getIdKorban()

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
                        Log.d("ID KORBAN 2 : ", it.child("idKorban").value.toString())

                        if (user == it.child("idKorban").value.toString()) {
                            getIdInfoKorban(it.key.toString())
                        }

                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)                }
            })

    }

    private fun getIdInfoKorban(idInfoKorban: String) {
        FirebaseDatabase.getInstance().getReference("KorbanTertolong")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val children = p0.children
                    children.forEach {

                        if (idInfoKorban == it.child("idInfoKorban").value.toString() &&
                            it.child("finished").value!!.toString() == "true") {
                            return

                        } else if (idInfoKorban == it.child("idInfoKorban").value.toString() &&
                            it.child("rescuerArrived").value!!.toString() == "true") {
                            getRescuerData(
                                it.child("idRescuer").value.toString(),
                                it.child("accepted").value!!.equals(true),
                                it.child("onTheWay").value!!.equals(true),
                                it.child("rescuerArrived").value!!.equals(true),
                                idInfoKorban, it.key.toString()
                            )
                            return

                        } else if (idInfoKorban == it.child("idInfoKorban").value.toString() &&
                            it.child("finished").value!!.toString() == "false") {
                            getRescuerData(
                                it.child("idRescuer").value.toString(),
                                it.child("accepted").value!!.equals(true),
                                it.child("onTheWay").value!!.equals(true),
                                it.child("rescuerArrived").value!!.equals(true),
                                idInfoKorban, it.key.toString()
                            )
                            checkStatus(it.key.toString(), idInfoKorban)
                            return
                        }
                    }
                    isKorbanTertolong(idInfoKorban)
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })
    }

    private fun isKorbanTertolong(idInfoKorban: String) {
        progressbar_name.visibility = View.GONE
        val statusSent = StatusSentFragment.newInstance()
        openFragment(statusSent)

        val ref = FirebaseDatabase.getInstance().getReference("KorbanTertolong")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                if (p0.child("idInfoKorban").value.toString() == idInfoKorban) {
                    getRescuerData(p0.child("idRescuer").value.toString(),
                        p0.child("accepted").value!!.equals(true),
                        p0.child("onTheWay").value!!.equals(true),
                        p0.child("rescuerArrived").value!!.equals(true),
                        p0.child("idInfoKorban").value!!.toString(),
                        p0.key.toString())
                    checkStatus(p0.key.toString(), idInfoKorban)
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun checkStatus(idKorbanTertolong: String, idInfoKorban: String) {
        val ref = FirebaseDatabase.getInstance().getReference("KorbanTertolong/$idKorbanTertolong")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("TemukanSayaError : ", p0.message)
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                Log.d("ON CHILD CHANGE :", p0.key.toString())
                if (p0.key.toString() == "rescuerArrived") {
                    updateFragment(
                        false,
                        false,
                        true,
                        idInfoKorban,
                        idKorbanTertolong
                    )
                } else if (p0.key.toString() == "onTheWay") {
                    updateFragment(
                        false,
                        true,
                        false,
                        idInfoKorban,
                        idKorbanTertolong
                    )
                }

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })

    }

    private fun updateFragment(isAccepted: Boolean, isRunning: Boolean, isRescuerArrived: Boolean, idInfoKorban: String, idKorbanTertolong: String) {
        if (isRescuerArrived) {
            val finishFragment = StatusFinishedFragment.newInstance(rescuer, idInfoKorban, idKorbanTertolong)
            openFragment(finishFragment)
        } else if (isRunning) {
            val runningFragment = StatusRunningFragment.newInstance(rescuer)
            openFragment(runningFragment)
        } else if (isAccepted) {
            val acceptedFragment = StatusAcceptedFragment.newInstance(idInfoKorban, idKorbanTertolong, rescuer)
            openFragment(acceptedFragment)
        }
    }

    private fun getRescuerData(idRescuer: String?, isAccepted: Boolean, isRunning: Boolean, isRescuerArrived: Boolean, idInfoKorban: String, idKorbanTertolong: String) {
        progressbar_name.visibility = View.GONE
        FirebaseDatabase.getInstance().reference.child("Rescuers/$idRescuer")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    rescuer = p0.getValue(Rescuer::class.java)!!
                    updateFragment(isAccepted, isRunning, isRescuerArrived, idInfoKorban, idKorbanTertolong)
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

}