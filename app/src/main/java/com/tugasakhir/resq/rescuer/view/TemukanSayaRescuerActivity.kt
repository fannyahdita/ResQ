package com.tugasakhir.resq.rescuer.view

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.activity_temukansaya_rescuer.*
import java.util.*

class TemukanSayaRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temukansaya_rescuer)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        layout_detail_marker.visibility = View.GONE

        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_map_rescuer)
                as SupportMapFragment

        FirebaseDatabase.getInstance().getReference("InfoKorban")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val children = p0.children
                    children.forEach {
                        val uid = it.key.toString()
                        val latitude = it.child("latitude").value.toString().toDouble()
                        val longitude = it.child("longitude").value.toString().toDouble()
                        val victimId = it.child("idKorban").value.toString()

                        setMaps(latitude, longitude, victimId, uid)

                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })
    }

    private fun setMaps(latitude: Double, longitude: Double, victimId: String, uid: String) {
        mapFragment.getMapAsync { it ->
            val location = LatLng(latitude, longitude)
            it.isMyLocationEnabled = true
            it.addMarker(MarkerOptions().position(location).title(victimId))
            it.animateCamera(CameraUpdateFactory.newLatLng(location))
            it.setOnMarkerClickListener {
                layout_detail_marker.visibility = View.VISIBLE
                button_close_detail.setOnClickListener {
                    layout_detail_marker.visibility = View.GONE
                }
                textview_victim_latitude_longitude.text = "${it.position.latitude}, ${it.position.longitude}"
                textview_victim_address.text =
                    getAddress(it.position.latitude, it.position.longitude)
                return@setOnMarkerClickListener true
            }
        }
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocode = Geocoder(this, Locale.getDefault())
        val address = geocode.getFromLocation(latitude, longitude, 1)
        return address[0].getAddressLine(0)
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