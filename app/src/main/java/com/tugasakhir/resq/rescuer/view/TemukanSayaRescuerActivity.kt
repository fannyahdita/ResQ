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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.InfoKorban
import com.tugasakhir.resq.korban.model.KorbanTertolong
import com.tugasakhir.resq.rescuer.VictimInfoData
import kotlinx.android.synthetic.main.activity_temukansaya_rescuer.*

class TemukanSayaRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var korban: InfoKorban
    private lateinit var victimInfoData: VictimInfoData
    private var idRescuer = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temukansaya_rescuer)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        victimInfoData = VictimInfoData()
        idRescuer = FirebaseAuth.getInstance().currentUser?.uid.toString()

        layout_detail_marker.visibility = View.GONE

        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_map_rescuer)
                as SupportMapFragment

        getAllDataVictim()

        button_close_detail.setOnClickListener { layout_detail_marker.visibility = View.GONE }
    }

    private fun setMaps(korban: InfoKorban, victimInfoId: String) {
        mapFragment.getMapAsync { gMap ->
            val location = LatLng(korban.latitude.toDouble(), korban.longitude.toDouble())
            gMap.isMyLocationEnabled = true
            gMap.addMarker(
                MarkerOptions().position(location).title(victimInfoId)
            )
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
            gMap.setOnMarkerClickListener { marker ->


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
                    victimInfoData.setDetailMaps(marker.title.toString(), this)
                }

                button_i_want_to_help.setOnClickListener {
                    makeHelpedVictim(idRescuer, marker.title.toString())
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
                        val child = it.child("jumlahAnak").value.toString().toInt()
                        val info = it.child("infoTambahan").value.toString()
                        val isFoodNeeded = it.child("bantuanMakanan").value.toString().toBoolean()
                        val isMedicNeeded = it.child("bantuanMedis").value.toString().toBoolean()
                        val isEvacuationNeeded =
                            it.child("bantuanEvakuasi").value.toString().toBoolean()
                        val uid = it.child("idKorban").value.toString()

                        korban = InfoKorban(
                            uid,
                            latitude,
                            longitude,
                            elderly,
                            adult,
                            child,
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

    private fun makeHelpedVictim(rescuerId: String, victimInfoId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("KorbanTertolong")
        val idHelpedVictim = ref.push().key.toString()

        val helpedVictim = KorbanTertolong(
            rescuerId, victimInfoId,
            isAccepted = true,
            isOnTheWay = false,
            isFinished = false
        )

        ref.child(idHelpedVictim).setValue(helpedVictim).addOnCompleteListener {
            if (it.isSuccessful) {
                setIsHelping()
                val intent = Intent(this, HelpVictimActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.d("KorbanTertolongError: ", it.exception?.message!!)
            }
        }
    }

    private fun setIsHelping() {
        FirebaseDatabase.getInstance().getReference("Rescuers")
            .child(idRescuer)
            .child("helping")
            .setValue(true)
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