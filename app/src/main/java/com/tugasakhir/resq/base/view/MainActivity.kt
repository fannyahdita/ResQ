package com.tugasakhir.resq.base.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.view.*
import com.tugasakhir.resq.rescuer.view.HelpVictimActivity
import com.tugasakhir.resq.rescuer.view.TemukanSayaRescuerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private var isKorban: Boolean = false
    private var isAskingHelp: Boolean = false
    private var isHelping: Boolean = false
    private var doubleBackToExitPressedOnce = false

    var long: String = ""
    var lat: String = ""
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            item.isCheckable = true
            when (item.itemId) {
                R.id.navigation_beranda -> {
                    actionBar.title = getString(R.string.app_name_actionbar)
                    val homeFragment = HomeFragment.newInstance()
                    openFragment(homeFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_posko -> {
                    actionBar.title = getString(R.string.posko_actionbar)
                    val poskoKorban = PoskoMapFragment.newInstance(isKorban, lat, long)
                    openFragment(poskoKorban)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_kontak -> {
                    actionBar.title = getString(R.string.contact_actionbar)
                    val callCenterFragment = CallCenterFragment.newInstance()
                    openFragment(callCenterFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_akun -> {
                    actionBar.title = getString(R.string.profile_actionbar)
                    val profileRescuerFragment = ProfileFragment.newInstance(isKorban)
                    openFragment(profileRescuerFragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image_loading_backgroung.postDelayed({
            image_loading_backgroung.visibility = View.GONE
            progressbar_loading.visibility = View.GONE
        }, 3000)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        val homeFragment = HomeFragment.newInstance()
        openFragment(homeFragment)

        val user = FirebaseAuth.getInstance().currentUser?.uid

        setIsHelping(user!!)

        FirebaseDatabase.getInstance().reference.child("AkunKorban/$user")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    isKorban = p0.exists()
                    if (isKorban) {
                        isAskingHelp = p0.child("askingHelp").value!!.toString() == "true"
                        Log.d("isAsking 1", isAskingHelp.toString())
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("DatabaseReference : ", "user with id $user is not exist")
                    Toast.makeText(this@MainActivity, p0.message, Toast.LENGTH_SHORT).show()
                }
            })


        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        actionBar = this.supportActionBar!!
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_logo_round)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.app_name_actionbar)
        actionBar.elevation = 0F

        navigation_temukan.setOnClickListener {
            Log.d("isHelpingDalamOnclick ", isHelping.toString())
            actionBar.title = getString(R.string.temukansaya_actionbar)
            disableNavigation(navigationView)
            showTemukanSaya(isKorban)
        }

    }

    private fun showTemukanSaya(isKorban: Boolean) {
        if (isKorban) {
            if (isAskingHelp) {
                val intent = Intent(this, StatusTemukanKorbanActivity::class.java)
                intent.putExtra(EXTRA_PREV_ACTIVITY, "Main")
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, TemukanSayaActivity::class.java)
                intent.putExtra(EXTRA_LAT, lat)
                intent.putExtra(EXTRA_LONG, long)
                startActivity(intent)
                finish()
            }
        } else {
            if (isHelping) {
                val intent = Intent(this, HelpVictimActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, TemukanSayaRescuerActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun disableNavigation(bottomNavigation: BottomNavigationView) {
        val menuBar: Menu = bottomNavigation.menu
        for (i in 0 until menuBar.size()) {
            if (i != 2) {
                menuBar.getItem(i).isCheckable = false
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.toast_exit), Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun setIsHelping(rescuerId: String) {
        Log.d("isHelping user", rescuerId)
        FirebaseDatabase.getInstance().getReference("Rescuers")
            .child(rescuerId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        isHelping = p0.child("helping").value.toString() == "true"
                        Log.d("isHelping 1", isHelping.toString())
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("IsHelpingRescuerError: ", p0.message)
                }
            })
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        long = location.longitude.toString()
                        lat = location.latitude.toString()
                    }
                }
            } else {
                Toast.makeText(this, "getString(R.string.turn_on_location) location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            long = mLastLocation.longitude.toString()
            lat = mLastLocation.latitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
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