package com.tugasakhir.resq.korban.view

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tugasakhir.resq.base.view.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.model.Rescuer
import kotlinx.android.synthetic.main.fragment_main_status_korban.*
import java.util.*


class StatusTemukanKorbanActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var rescuer: Rescuer
    private lateinit var notificationManager: NotificationManager

    private val channelId = "com.tugasakhir.resq.korban.view"
    private val description = "status notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_main_status_korban)
        progressbar_name.visibility = View.VISIBLE

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        updateStatus()
        getIdKorban()

    }

    override fun onResume() {
        super.onResume()
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
                    var index = 1
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
                        } else if (p0.childrenCount == index.toLong()) {
                            isKorbanTertolong(idInfoKorban)
                        }
                        index++
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })
    }

    private fun isKorbanTertolong(idInfoKorban: String) {
        progressbar_name.visibility = View.GONE
        val statusSent = StatusSentFragment.newInstance(idInfoKorban)
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
                    sendNotification(getString(R.string.status_2), "Tim penolong telah menerima permintaan bantuanmu dan akan segera menuju ke tempatmu")
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
                    sendNotification("Permintaan bantuan selesai", "Semoga bantuan yang diberikan bermanfaat")
                    updateFragment(
                        false,
                        false,
                        true,
                        idInfoKorban,
                        idKorbanTertolong
                    )
                } else if (p0.key.toString() == "accepted") {
                    sendNotification(getString(R.string.status_3), rescuer.name +" sudah menuju ke tempatmu")
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
            val runningFragment = StatusRunningFragment.newInstance(rescuer, idKorbanTertolong)
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
        transaction.commitAllowingStateLoss()
    }

    private fun sendNotification(title: String, text: String) {
        val intent = Intent(applicationContext, StatusTemukanKorbanActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            val builder = Notification.Builder(applicationContext, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setSubText(getCurrentDateTime().toString("HH:mm"))
                .setSmallIcon(R.drawable.ic_logo_transparent)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        applicationContext?.resources,
                        R.drawable.ic_logo_round
                    )
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            notificationManager.notify(1234, builder.build())
        } else {
            val builder = Notification.Builder(applicationContext)
                .setContentTitle(title)
                .setContentText(text)
                .setSubText(getCurrentDateTime().toString("HH:mm"))
                .setSmallIcon(R.drawable.ic_logo_transparent)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        applicationContext?.resources,
                        R.drawable.ic_logo_round
                    )
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            notificationManager.notify(1234, builder.build())
        }
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    private fun Date.toString(s: String, locale: Locale = Locale.getDefault()): String {
        val formatter = java.text.SimpleDateFormat(s, locale)
        return formatter.format(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}