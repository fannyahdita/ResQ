package com.tugasakhir.resq.rescuer.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.InfoKorban
import com.tugasakhir.resq.korban.model.KorbanTertolong
import com.tugasakhir.resq.rescuer.VictimInfoData
import kotlinx.android.synthetic.main.activity_temukansaya_rescuer.*
import java.util.*

class TemukanSayaRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var korban: InfoKorban
    private lateinit var victimInfoData: VictimInfoData
    private var isAccepted: Boolean = false
    private var isOnTheWay: Boolean = false
    private var isRescuerArrived: Boolean = false
    private var idRescuer = ""
    private val permissionId = 42

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

    private fun setMaps(korban: InfoKorban, victimInfoId: String, isAccepted: Boolean, isOnTheWay: Boolean, isRescuerArrived: Boolean) {
        Log.d("MASUK SETMAPS", victimInfoId)
        mapFragment.getMapAsync { gMap ->
            val location = LatLng(korban.latitude.toDouble(), korban.longitude.toDouble())
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
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
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
                textview_victim_latitude_longitude.text = Html.fromHtml(
                    getString(
                        R.string.lat_long,
                        marker.position.latitude.toString(),
                        marker.position.longitude.toString()
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
                        val latitude = info.child("latitude").value.toString()
                        val longitude = info.child("longitude").value.toString()
                        val elderly = info.child("jumlahLansia").value.toString().toInt()
                        val adult = info.child("jumlahDewasa").value.toString().toInt()
                        val child = info.child("jumlahAnak").value.toString().toInt()
                        val additionalInfo = info.child("infoTambahan").value.toString()
                        val isFoodNeeded = info.child("bantuanMakanan").value.toString().toBoolean()
                        val isMedicNeeded = info.child("bantuanMedis").value.toString().toBoolean()
                        val isEvacuationNeeded =
                            info.child("bantuanEvakuasi").value.toString().toBoolean()
                        val uid = info.child("idKorban").value.toString()

                        korban = InfoKorban(
                            uid,
                            latitude,
                            longitude,
                            elderly,
                            adult,
                            child,
                            additionalInfo,
                            isFoodNeeded,
                            isMedicNeeded,
                            isEvacuationNeeded
                        )

                        getStatus(korban, info.key.toString())

                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })
    }

    private fun getStatus(korban: InfoKorban, victimInfoId: String) {
        Log.d("MASUK GETSTATUS", victimInfoId)
        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child("KorbanTertolong").exists()) {
                    val helpedVictim = p0.child("KorbanTertolong")
                    helpedVictim.children.forEach { helped ->
                        if (victimInfoId == helped.child("idInfoKorban").value) {
                            Log.d("masuk sini", (victimInfoId == helped.child("idInfoKorban").value.toString()).toString())
                            Log.d("rescuer arrived 1", (helped.child("rescuerArrived").value.toString()))
                            isAccepted = helped.child("accepted").value.toString().toBoolean()
                            isOnTheWay = helped.child("onTheWay").value.toString().toBoolean()
                            isRescuerArrived = helped.child("rescuerArrived").value.toString().toBoolean()
                            setMaps(korban, victimInfoId, isAccepted, isOnTheWay, isRescuerArrived)
                        }
                    }
                } else {
                    Log.d("korban tertolong", "kosong")
                    Log.d("rescuer arrived, korban tertolong kosong", "rescuerArrived false dong")
                    isAccepted = false
                    isOnTheWay = false
                    isRescuerArrived = false
                    setMaps(korban, victimInfoId, isAccepted, isOnTheWay, isRescuerArrived)
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
            isRescuerArrived = false,
            isFinished = false,
            date = victimInfoData.getCurrentDateTime().toString("dd/MM/yyy HH:mm")
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

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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
}

private fun Date.toString(s: String, locale: Locale = Locale.getDefault()): String {
    val formatter = java.text.SimpleDateFormat(s, locale)
    return formatter.format(this)
}
