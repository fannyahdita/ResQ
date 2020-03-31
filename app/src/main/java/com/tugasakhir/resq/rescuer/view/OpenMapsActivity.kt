package com.tugasakhir.resq.rescuer.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R

class OpenMapsActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var mapFragment: SupportMapFragment
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val permissionId = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_maps)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.open_maps_actionbar)
        actionBar.elevation = 0F

        val bundle = intent.extras
        val location = bundle?.getString("location")?.split(" ")
        latitude = location!![0].toDouble()
        longitude = location[1].toDouble()

        Log.d("openmaps", "$latitude $longitude")

        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_open_map)
                as SupportMapFragment

        setMaps(latitude, longitude)
    }

    private fun setMaps(latitude: Double, longitude: Double) {
        mapFragment.getMapAsync {
            val location = LatLng(latitude, longitude)
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                    it.isMyLocationEnabled = true
                } else {
                    Toast.makeText(this, "Please turn on your location", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                requestPermissions()
            }

            it.addMarker(MarkerOptions().position(location))
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}