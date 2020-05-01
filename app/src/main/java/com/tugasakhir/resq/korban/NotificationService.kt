package com.tugasakhir.resq.korban

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.AkunKorban
import com.tugasakhir.resq.rescuer.model.Chat
import com.tugasakhir.resq.rescuer.model.Rescuer
import com.tugasakhir.resq.rescuer.view.ChatMessageRescuerActivity
import com.tugasakhir.resq.rescuer.view.ChatMessageVictimActivity
import java.io.Serializable

class NotificationService : Service() {

    private val channelId = "com.tugasakhir.resq.rescuer.view"
    private val description = "test notification"

    private lateinit var rescuer: Rescuer
    private lateinit var victim: AkunKorban
    private var idHelpedVictim = ""
    private var isVictim = false

    private lateinit var runningProcess : List<ActivityManager.RunningAppProcessInfo>
    private lateinit var taskInfo : List<ActivityManager.RunningTaskInfo>


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        idHelpedVictim = intent?.getStringExtra("id")!!

        if (intent?.getStringExtra("previous") == "victim") {
            rescuer = intent.getSerializableExtra("rescuer") as Rescuer
            isVictim = true
        } else {
            victim = intent.getSerializableExtra("victim") as AkunKorban
        }

        val fromId = FirebaseAuth.getInstance().currentUser?.uid
        val toId = if (isVictim) {
            rescuer.id
        } else {
            victim.id
        }

        val ref =
            FirebaseDatabase.getInstance().getReference("Messages/$idHelpedVictim/$fromId/$toId")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val childrenLength = p0.childrenCount.toInt()
                checkingChat(fromId, toId, childrenLength)
            }
        })



        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        val intentBroadcast = Intent(this, NotificationReceiver::class.java)
        intentBroadcast.putExtra("id", idHelpedVictim)
        if (isVictim) {
            intentBroadcast.putExtra("rescuer", rescuer as Serializable)
        } else {
            intentBroadcast.putExtra("victim", victim as Serializable)
        }
        intentBroadcast.putExtra("previous", "victim")
        sendBroadcast(intentBroadcast)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun checkingChat(fromId : String?, toId : String, childrenLength : Int) {

        val ref =
            FirebaseDatabase.getInstance().getReference("Messages/$idHelpedVictim/$fromId/$toId")

        var index = 0

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chat = p0.getValue(Chat::class.java)

                if (chat != null && childrenLength < ++index) {
                    Toast.makeText(applicationContext, index.toString(), Toast.LENGTH_SHORT).show()
                    if (chat.fromId != FirebaseAuth.getInstance().uid) {
                        sendNotification(chat.text, isAppInBackground(), chat.time)
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
        taskInfo = am.getRunningTasks(1)
        val componentInfo = taskInfo.get(0).topActivity
        if (componentInfo?.className.equals(ChatMessageRescuerActivity::class.java.name) ||
            componentInfo?.className.equals(ChatMessageVictimActivity::class.java.name)) {

            isInBackground = false
        }
        return isInBackground
    }



    private fun sendNotification(text: String?, isInBackground : Boolean, time: String) {
        val intent: Intent
        val name: String

        if (isVictim) {
            intent = Intent(applicationContext, ChatMessageVictimActivity::class.java)
            intent.putExtra("id", idHelpedVictim)
            intent.putExtra("rescuer", rescuer as Serializable)
            name = rescuer.name
        } else {
            intent = Intent(applicationContext, ChatMessageRescuerActivity::class.java)
            intent.putExtra("id", idHelpedVictim)
            intent.putExtra("victim", victim as Serializable)
            name = victim.name
        }

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
                .setContentTitle(name)
                .setContentText(text)
                .setSubText(time)
                .setSmallIcon(R.drawable.ic_logo_transparent)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        applicationContext?.resources,
                        R.drawable.ic_logo_round
                    )
                )
                .setContentIntent(pendingIntent)

            notificationManager.notify(1234, builder.build())
        }
    }
}