package com.tugasakhir.resq.korban

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.model.Chat
import com.tugasakhir.resq.rescuer.model.Rescuer
import java.io.Serializable

class NotificationService : Service() {

    private val channelId = "com.tugasakhir.resq.rescuer.view"
    private val description = "test notification"

    private lateinit var rescuer: Rescuer
    private var idHelpedVictim = ""

    private lateinit var runningProcess : List<ActivityManager.RunningAppProcessInfo>
    private lateinit var taskInfo : List<ActivityManager.RunningTaskInfo>


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        idHelpedVictim = intent?.getStringExtra("id")!!
        rescuer = intent.getSerializableExtra("rescuer") as Rescuer

        checkingChat()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        val intentBroadcast = Intent(this, NotificationReceiver::class.java)
        intentBroadcast.putExtra("id", idHelpedVictim)
        intentBroadcast.putExtra("rescuer", rescuer as Serializable)
        sendBroadcast(intentBroadcast)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun checkingChat() {
        val fromId = FirebaseAuth.getInstance().currentUser?.uid
        val toId = rescuer.id
        val ref =
            FirebaseDatabase.getInstance().getReference("Messages/$idHelpedVictim/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chat = p0.getValue(Chat::class.java)

                if (chat != null) {
                    if (chat.fromId != FirebaseAuth.getInstance().uid) {
                        sendNotification(chat.text, isAppInBackground())
                    }
                }
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildRemoved(p0: DataSnapshot) {}
        })

    }

    private fun isAppInBackground() : Boolean {
        var isInBackground = true

        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        runningProcess = am.runningAppProcesses
        for (processInfo in runningProcess) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                var index = 1
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess.equals(packageName)) {
                        isInBackground = false
                    }
                    index++
                }
            } else {
                taskInfo = am.getRunningTasks(1)
                val componentInfo = taskInfo.get(0).topActivity
                if (componentInfo?.packageName.equals(packageName)) {
                    isInBackground = false
                }
            }
        }
        return isInBackground
    }



    private fun sendNotification(text: String?, isInBackground : Boolean) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isInBackground) {
            val notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            val builder = Notification.Builder(applicationContext, channelId)
                .setContentTitle("Anda mendapat pesan baru")
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_akun_icon)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        applicationContext?.resources,
                        R.drawable.ic_beranda_icon
                    )
                )
                .setContentIntent(pendingIntent)

            notificationManager.notify(1234, builder.build())
        }
    }
}