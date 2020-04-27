package com.tugasakhir.resq.korban

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tugasakhir.resq.rescuer.model.Rescuer
import java.io.Serializable

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getStringExtra("id")
        val rescuer = intent.getSerializableExtra("rescuer") as Rescuer

        val intentService = Intent(context, NotificationService::class.java)
        intentService.putExtra("id", id)
        intentService.putExtra("rescuer", rescuer as Serializable)
        context.startService(intentService)

    }
}