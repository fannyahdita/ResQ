package com.tugasakhir.resq.korban

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tugasakhir.resq.korban.model.AkunKorban
import com.tugasakhir.resq.rescuer.model.Rescuer
import java.io.Serializable

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getStringExtra("id")
        val previous = intent.getStringExtra("previous")

        val intentService = Intent(context, NotificationService::class.java)

        if (previous == "victim") {
            val rescuer = intent.getSerializableExtra("rescuer") as Rescuer
            intentService.putExtra("rescuer", rescuer as Serializable)
        } else {
            val victim = intent.getSerializableExtra("victim") as AkunKorban
            intentService.putExtra("victim", victim as Serializable)
        }

        intentService.putExtra("id", id)
        intentService.putExtra("previous", previous)
        context.startService(intentService)

    }
}