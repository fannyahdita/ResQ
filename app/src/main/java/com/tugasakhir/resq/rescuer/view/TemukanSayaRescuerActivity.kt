package com.tugasakhir.resq.rescuer.view

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.InfoKorban
import com.tugasakhir.resq.korban.model.KorbanTertolong
import com.tugasakhir.resq.rescuer.helper.VictimInfoData
import kotlinx.android.synthetic.main.activity_temukansaya_rescuer.*
import java.util.*

class TemukanSayaRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var korban: InfoKorban
    private lateinit var victimInfoData: VictimInfoData
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var idRescuer = ""
    private var currLat: Double = 0.0
    private var currLong: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temukansaya_rescuer)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setLocation()

        victimInfoData = VictimInfoData()
        idRescuer = FirebaseAuth.getInstance().currentUser?.uid.toString()

        layout_detail_marker.visibility = View.GONE

        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_map_rescuer)
                as SupportMapFragment

        button_close_detail.setOnClickListener { layout_detail_marker.visibility = View.GONE }

        getAllDataVictim()
    }

    private fun setMaps(
        korban: InfoKorban,
        victimInfoId: String,
        isAccepted: Boolean,
        isOnTheWay: Boolean,
        isRescuerArrived: Boolean
    ) {
        mapFragment.getMapAsync { gMap ->
            val location = LatLng(korban.latitude.toDouble(), korban.longitude.toDouble())
            val currLocation = LatLng(currLat, currLong)
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 15F))
            gMap.isMyLocationEnabled = true
            when {
                isAccepted or isOnTheWay -> {
                    Log.d(victimInfoId, "isAccepted or isOnTheWay")
                    gMap.addMarker(
                        MarkerOptions().position(location).title("$victimInfoId true")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    )
                }
                isRescuerArrived -> {
                    Log.d(victimInfoId, "$isRescuerArrived Finished gak muncul")
                    gMap.addMarker(MarkerOptions().position(location).visible(false))
                }
                else -> {
                    Log.d(victimInfoId, "$isRescuerArrived tolong aku")
                    gMap.addMarker(
                        MarkerOptions().position(location).title("$victimInfoId false")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )
                }
            }

            gMap.setOnMarkerClickListener { marker ->
                val idStatus = marker.title.toString().split(" ")
                if (idStatus[1].toBoolean()) {
                    Toast.makeText(
                        this,
                        getString(R.string.victim_already_handled),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnMarkerClickListener true
                }
                layout_detail_marker.visibility = View.VISIBLE
                textview_victim_distance.text = Html.fromHtml(
                    getString(
                        R.string.distance,
                        getDistance(marker.position.latitude, marker.position.longitude)
                    )
                )
                textview_victim_address.text =
                    victimInfoData.getAddress(
                        marker.position.latitude,
                        marker.position.longitude,
                        this
                    )
                button_detail_victim.setOnClickListener {
                    victimInfoData.setDetailMaps(idStatus[0], this)
                }

                button_i_want_to_help.setOnClickListener {
                    makeHelpedVictim(idRescuer, idStatus[0])
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
                    children.forEach { info ->
                        korban = info.getValue(InfoKorban::class.java)!!
                        getStatus(korban, info.key.toString())
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })
    }

    private fun getStatus(korban: InfoKorban, victimInfoId: String) {
        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child("KorbanTertolong").exists()) {
                    val helpedVictim = p0.child("KorbanTertolong")
                    var index = 1
                    helpedVictim.children.forEach { helped ->
                        if (victimInfoId == helped.child("idInfoKorban").value.toString()) {
                            val isAccepted = helped.child("accepted").value.toString().toBoolean()
                            val isOnTheWay = helped.child("onTheWay").value.toString().toBoolean()
                            val isRescuerArrived =
                                helped.child("rescuerArrived").value.toString().toBoolean()
                            setMaps(korban, victimInfoId, isAccepted, isOnTheWay, isRescuerArrived)
                            index++
                            return
                        } else if (helpedVictim.childrenCount == index.toLong()) {
                            val isAccepted = false
                            val isOnTheWay = false
                            val isRescuerArrived = false
                            setMaps(korban, victimInfoId, isAccepted, isOnTheWay, isRescuerArrived)
                        }
                        index++
                    }
                } else {
                    val isAccepted = false
                    val isOnTheWay = false
                    val isRescuerArrived = false
                    setMaps(korban, victimInfoId, isAccepted, isOnTheWay, isRescuerArrived)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
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
            isRescuerArrived = false,
            isFinished = false,
            date = victimInfoData.getCurrentDateTime().toString("dd/MM/yyy HH:mm")
        )

        ref.child(idHelpedVictim).setValue(helpedVictim).addOnCompleteListener {
            if (it.isSuccessful) {
                setIsHelping()
                val intent = Intent(this, HelpVictimActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Log.d("KorbanTertolongError: ", it.exception?.message!!)
            }
        }
    }

    private fun getDistance(victimLat: Double, victimLong: Double) : String {
        val result = FloatArray(1)
        Location.distanceBetween(currLat, currLong, victimLat, victimLong, result)

        val poskoDistance = result[0].toString().split(".")[0]
        var fixDistance = ""

        fixDistance = if (poskoDistance.length > 3) {
            (poskoDistance.toInt() / 1000).toString() + " km"
        } else {
            "$poskoDistance m"
        }

        return fixDistance
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
                onBackPressed()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setLocation() {
        fusedLocationClient.lastLocation.addOnCompleteListener(this) {
            val location = it.result
            if (location == null) {
                requestNewLocationData()
            } else {
                currLat = location.latitude
                currLong = location.longitude
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            currLong = mLastLocation.longitude
            currLat = mLastLocation.latitude
        }
    }
}

private fun Date.toString(s: String, locale: Locale = Locale.getDefault()): String {
    val formatter = java.text.SimpleDateFormat(s, locale)
    return formatter.format(this)
}