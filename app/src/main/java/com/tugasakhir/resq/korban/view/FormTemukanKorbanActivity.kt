package com.tugasakhir.resq.korban.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.activity_temukansayaform_korban.*

class FormTemukanKorbanActivity : AppCompatActivity() {

    private lateinit var actionBar : ActionBar
    var lansia_displayInt : Int = 0
    var anak_displayInt : Int = 0
    var dewasa_displayInt : Int = 0
    var status: Int = 0
    var long: String = ""
    var lat: String = ""
    var isEvakuasi: Boolean = false
    var isMakanan: Boolean = false
    var isMedis: Boolean = false
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temukansayaform_korban)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        radio_jenisbantuan.setOnCheckedChangeListener { group, checkedId ->
            if (R.id.rb_evakuasi == checkedId) {
                isEvakuasi = true
            } else if (R.id.rb_makanan == checkedId) {
                isMakanan = true
            } else if (R.id.rb_medis == checkedId) {
                isMedis = true
            }
        }

        button_send.setOnClickListener {
            getDataKorban()
//            val intent = Intent(this, Status1TemukanKorbanActivity::class.java)
//            startActivity(intent)
        }

        decrease_lansia.setOnClickListener {
            if (lansia_displayInt != 0) {
                lansia_displayInt--
            }
            status = 1
            displayNumber(lansia_displayInt)
        }

        decrease_anak.setOnClickListener {
            if (anak_displayInt != 0) {
                anak_displayInt--
            }
            status = 3
            displayNumber(anak_displayInt)
        }

        decrease_dewasa.setOnClickListener {
            if (dewasa_displayInt != 0) {
                dewasa_displayInt--
            }
            status = 2
            displayNumber(dewasa_displayInt)
        }

        increase_lansia.setOnClickListener {
            lansia_displayInt++
            status = 1
            displayNumber(lansia_displayInt)
        }

        increase_anak.setOnClickListener {
            anak_displayInt++
            status = 3
            displayNumber(anak_displayInt)
        }

        increase_dewasa.setOnClickListener {
            dewasa_displayInt++
            status = 2
            displayNumber(dewasa_displayInt)
        }
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

    private fun displayNumber(number: Int) {
        if (status == 3) {
            integer_number_anak.setText("" + number)
        } else if (status == 2) {
            integer_number_dewasa.setText("" + number)
        } else {
            integer_number_lansia.setText("" + number)
        }
    }

    private fun getDataKorban() {
        val lansia = integer_number_lansia.text.toString().trim()
        val dewasa = integer_number_dewasa.text.toString().trim()
        val anak = integer_number_anak.text.toString().trim()
        val infoTambahan = edittext_infotambahan.text.toString().trim()

        val builder = AlertDialog.Builder(this@FormTemukanKorbanActivity)
        builder.setTitle("Datanya")
        builder.setMessage("Latitude" + lat + " Longitude" + long + " jumlah Lansia " + lansia + " jumlah dewasa " + dewasa + " jumlah anak " + anak +
            " info tambahan " + infoTambahan + " evakuasi " + isEvakuasi.toString() + " makanan " + isMakanan.toString() + " medis " + isMedis.toString())
        builder.setNeutralButton("Oke"){_,_ ->

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        long = location.longitude.toString()
                        lat = location.latitude.toString()
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
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            long = mLastLocation.longitude.toString()
            lat = mLastLocation.latitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

}