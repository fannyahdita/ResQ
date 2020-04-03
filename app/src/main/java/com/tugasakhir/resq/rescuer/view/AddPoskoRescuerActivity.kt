package com.tugasakhir.resq.rescuer.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.VictimInfoData
import com.tugasakhir.resq.rescuer.model.Posko
import kotlinx.android.synthetic.main.activity_add_posko_rescuer.*
import java.util.*

class AddPoskoRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var victimInfoData: VictimInfoData
    private lateinit var posko: Posko
    lateinit var fusedLocationClient: FusedLocationProviderClient
    private var idRescuer: String = ""
    private val permissionId = 42
    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_posko_rescuer)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.add_posko_actionbar)
        actionBar.elevation = 0F

        victimInfoData = VictimInfoData()
        idRescuer = FirebaseAuth.getInstance().currentUser?.uid.toString()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_edit_posko_address)
                as SupportMapFragment

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        longitude = location.longitude
                        latitude = location.latitude
                        setMaps()
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on your location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }

        button_add_posko_finish.setOnClickListener {
            validatePosko()
        }
    }

    private fun setMaps() {
        mapFragment.getMapAsync {
            it.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latitude!!, longitude!!),
                    17f
                )
            )
            edittext_edit_posko_address.setText(getAddress(latitude!!, longitude!!))
            it.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDrag(p0: Marker?) {
                    Log.d("Marker", "Dragging")
                }

                override fun onMarkerDragStart(p0: Marker?) {
                    Log.d("Marker", "Started")
                }

                override fun onMarkerDragEnd(p0: Marker?) {
                    val location = p0?.position
                    latitude = location?.latitude
                    longitude = location?.longitude
                }
            })
            it.setOnCameraIdleListener {
                val center = it.cameraPosition.target
                latitude = center.latitude
                longitude = center.longitude
                edittext_edit_posko_address.setText(getAddress(latitude!!, longitude!!))
            }
        }
    }

    private fun validatePosko() {
        val poskoName = edittext_edit_posko_name.text.toString().trim()
        val city = edittext_edit_posko_city.text.toString().trim()
        val district = edittext_edit_posko_district.text.toString().trim()
        val subDistrict = edittext_edit_posko_sub_district.text.toString().trim()
        val address = edittext_edit_posko_address.text.toString().trim()
        val capacity = edittext_edit_posko_capacity.text.toString().trim()
        val contactName = edittext_edit_posko_contact_name.text.toString().trim()
        val contactNumber = edittext_edit_posko_contact_number.text.toString().trim()
        var hasMedic = false
        var hasBed = false
        var hasKitchen = false
        var hasLogistic = false
        var hasWC = false

        if (poskoName.isEmpty()) {
            edittext_edit_posko_name.error = getString(R.string.field_is_empty)
            edittext_edit_posko_name.requestFocus()
            return
        }

        if (city.isEmpty()) {
            edittext_edit_posko_city.error = getString(R.string.field_is_empty)
            edittext_edit_posko_city.requestFocus()
            return
        }

        if (district.isEmpty()) {
            edittext_edit_posko_district.error = getString(R.string.field_is_empty)
            edittext_edit_posko_district.requestFocus()
            return
        }

        if (subDistrict.isEmpty()) {
            edittext_edit_posko_sub_district.error = getString(R.string.field_is_empty)
            edittext_edit_posko_sub_district.requestFocus()
            return
        }

        if (address.isEmpty()) {
            edittext_edit_posko_address.error = getString(R.string.field_is_empty)
            edittext_edit_posko_address.requestFocus()
            return
        }

        if (capacity.isEmpty()) {
            edittext_edit_posko_capacity.error = getString(R.string.field_is_empty)
            edittext_edit_posko_capacity.requestFocus()
            return
        }

        if (contactName.isEmpty()) {
            edittext_edit_posko_contact_name.error = getString(R.string.field_is_empty)
            edittext_edit_posko_contact_name.requestFocus()
            return
        }

        if (contactNumber.isEmpty()) {
            edittext_edit_posko_contact_number.error = getString(R.string.field_is_empty)
            edittext_edit_posko_contact_number.requestFocus()
            return
        }

        if (checkbox_medic.isChecked) {
            hasMedic = true
        }

        if (checkbox_bed.isChecked) {
            hasBed = true
        }

        if (checkbox_kitchen.isChecked) {
            hasKitchen = true
        }

        if (checkbox_logistic.isChecked) {
            hasLogistic = true
        }

        if (checkbox_wc.isChecked) {
            hasWC = true
        }

        val date = victimInfoData.getCurrentDateTime().toString("dd/MM/yyy HH:mm")

        posko = Posko(
            idRescuer, latitude!!, longitude!!, poskoName, city, district, subDistrict,
            address, capacity.toLong(), hasMedic, hasKitchen, hasWC, hasLogistic, hasBed, date,
            contactName, contactNumber, true
        )

        addPosko(posko)
    }

    private fun addPosko(posko: Posko) {
        val ref = FirebaseDatabase.getInstance().getReference("Posko")
        val idPosko = ref.push().key.toString()

        ref.child(idPosko).setValue(posko).addOnCompleteListener {
            if (it.isSuccessful) {
                onBackPressed()
                finish()
            } else {
                Log.d("AddNewPosko: ", it.exception?.message!!)
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
                onBackPressed()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val request = LocationRequest()
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request.interval = 0
        request.fastestInterval = 0
        request.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            request,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            longitude = lastLocation.longitude
            latitude = lastLocation.latitude
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
}

private fun Date.toString(s: String, locale: Locale = Locale.getDefault()): String {
    val formatter = java.text.SimpleDateFormat(s, locale)
    return formatter.format(this)
}