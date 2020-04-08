package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.model.Posko
import kotlinx.android.synthetic.main.fragment_detailposko_korban.*

const val EXTRA_POSKO = "com.tugasakhir.resq.korban.POSKO"

class PoskoDetailActivity: AppCompatActivity() {

    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_detailposko_korban)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.otp_actionbar)
        actionBar.elevation = 0F

        val posko = intent.extras?.get("EXTRA_POSKO") as Posko
        val currentLat = intent.getStringExtra("EXTRA_LAT")
        val currentLong = intent.getStringExtra("EXTRA_LONG")

        getRescuerData(posko?.idRescuer)

        textview_lokasi_posko.text = posko?.poskoName
        textview_nilai_kapasitas.text = posko?.capacity.toString()
        textview_alamat_posko.text = posko?.address

        tombol_petunjuk.setOnClickListener {
            val uri = "http://maps.google.com/maps?saddr=" + currentLat + "," + currentLong + "&daddr=" + posko?.latitude + "," + posko?.longitude
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
            finish()

        }
    }

    private fun getRescuerData(idRescuer: String?) {
        FirebaseDatabase.getInstance().reference.child("Rescuers/$idRescuer")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    textview_penambah.text = p0.child("name").value.toString()
                    textview_nilai_kontak.text = p0.child("phone").value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}