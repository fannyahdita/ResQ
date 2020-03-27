package com.tugasakhir.resq.rescuer.view

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.Korban
import com.tugasakhir.resq.rescuer.VictimInfoData
import kotlinx.android.synthetic.main.activity_temukansaya_rescuer.*

class TemukanSayaRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var korban: Korban
    private lateinit var victimInfoData: VictimInfoData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temukansaya_rescuer)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        victimInfoData = VictimInfoData()

        layout_detail_marker.visibility = View.GONE

        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_map_rescuer)
                as SupportMapFragment

        getAllDataVictim()

        button_close_detail.setOnClickListener { layout_detail_marker.visibility = View.GONE }
    }

    private fun setMaps(korban: Korban, victimInfoId: String) {
        mapFragment.getMapAsync { gMap ->
            val location = LatLng(korban.latitude.toDouble(), korban.longitude.toDouble())
            gMap.isMyLocationEnabled = true
            gMap.addMarker(
                MarkerOptions().position(location).title("$victimInfoId-${korban.idKorban}")
            )
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
            gMap.setOnMarkerClickListener { marker ->

                val allIds = marker.title.split("-")

                layout_detail_marker.visibility = View.VISIBLE
                textview_victim_latitude_longitude.text = Html.fromHtml(
                    getString(
                        R.string.lat_long,
                        marker.position.latitude.toString(), marker.position.longitude.toString()
                    )
                )
                textview_victim_address.text =
                    victimInfoData.getAddress(
                        marker.position.latitude,
                        marker.position.longitude,
                        this
                    )
                button_detail_victim.setOnClickListener {
                    victimInfoData.setDetailMaps(allIds[0], this)
                }

                button_i_want_to_help.setOnClickListener {
                    //kirim id infoKorban
                    //intent ke help victim
                    //bikin korbantertolong
                }
                return@setOnMarkerClickListener true
            }
        }
    }

    private fun getAllDataVictim() {
        FirebaseDatabase.getInstance().getReference("InfoKorban")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val children = p0.children
                    children.forEach {
                        val latitude = it.child("latitude").value.toString()
                        val longitude = it.child("longitude").value.toString()
                        val elderly = it.child("jumlahLansia").value.toString().toInt()
                        val adult = it.child("jumlahDewasa").value.toString().toInt()
                        val children = it.child("jumlahAnak").value.toString().toInt()
                        val info = it.child("infoTambahan").value.toString()
                        val isFoodNeeded = it.child("bantuanMakanan").value.toString().toBoolean()
                        val isMedicNeeded = it.child("bantuanMedis").value.toString().toBoolean()
                        val isEvacuationNeeded =
                            it.child("bantuanEvakuasi").value.toString().toBoolean()
                        val uid = it.child("idKorban").toString()

                        korban = Korban(
                            uid,
                            latitude,
                            longitude,
                            false,
                            elderly,
                            adult,
                            children,
                            info,
                            isFoodNeeded,
                            isMedicNeeded,
                            isEvacuationNeeded
                        )

                        setMaps(korban, it.key.toString())

                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}