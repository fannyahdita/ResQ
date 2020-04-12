package com.tugasakhir.resq.korban.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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

    private lateinit var mapFragment: SupportMapFragment
    private val permissionId = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_detailposko_korban)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.otp_actionbar)
        actionBar.elevation = 0F

        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_map)
                as SupportMapFragment

        val posko = intent.extras?.get("EXTRA_POSKO") as Posko
        val currentLat = intent.getStringExtra("EXTRA_LAT")
        val currentLong = intent.getStringExtra("EXTRA_LONG")

        getRescuerData(posko?.idRescuer)

        var facility = ""
        if (posko?.hasBed) {
            facility += getString(R.string.add_posko_radio_button_bed) + "\n"
        }
        if (posko?.hasKitchen) {
            facility += getString(R.string.add_posko_radio_button_kitchen) + "\n"
        }
        if (posko?.hasLogistic) {
            facility += getString(R.string.add_posko_radio_button_logistic) + "\n"
        }
        if (posko?.hasMedic) {
            facility += getString(R.string.add_posko_radio_button_medic) + "\n"
        }
        if (posko?.hasWC) {
            facility += getString(R.string.add_posko_radio_button_wc) + "\n"
        }

        textview_lokasi_posko.text = posko?.poskoName
        textview_nilai_kapasitas.text = Html.fromHtml(getString(R.string.number_of_kk, posko?.capacity.toString()))
        textview_alamat_posko.text = posko?.mapAddress
        textview_notes_posko.text = posko?.noteAddress
        textview_fasilitas_value.text = facility

        setMaps(posko?.latitude, posko?.longitude, posko?.poskoName)

        tombol_petunjuk.setOnClickListener {
            val uri = "http://maps.google.com/maps?saddr=" + currentLat + "," + currentLong + "&daddr=" + posko?.latitude + "," + posko?.longitude
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
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

    private fun setMaps(latitude: Double, longitude: Double, poskoName: String) {
        mapFragment.getMapAsync { gMap ->
            val location = LatLng(latitude, longitude)
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                    gMap.isMyLocationEnabled = true
                } else {
                    Toast.makeText(this, "Please turn on your location", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                requestPermissions()
            }
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
            gMap.addMarker(
                MarkerOptions().position(location).title(poskoName)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            ).showInfoWindow()

            gMap.setOnMarkerClickListener { marker ->
                return@setOnMarkerClickListener true
            }
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
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