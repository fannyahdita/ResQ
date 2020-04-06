package com.tugasakhir.resq.korban.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tugasakhir.resq.R
import android.Manifest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

class PoskoMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private val permissionId = 42

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!

        return inflater.inflate(R.layout.fragment_posko_korban, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.fragment_map_rescuer) as SupportMapFragment
        mapFragment?.getMapAsync(this)


//        mapFragment = fragmentManager!!.findFragmentById(R.id.fragment_map_rescuer)
//                as SupportMapFragment

//        setMaps("-6.3302658", "106.8388629")

    }

    private fun setMaps(lat: String, long: String) {
        mapFragment.getMapAsync { gMap ->
            val location = LatLng(lat.toDouble(), long.toDouble())

            if(checkPermissions()) {
                if (isLocationEnabled()) {
                    gMap.isMyLocationEnabled = true
                } else {
                    Toast.makeText(activity, "Please turn on your location", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                requestPermissions()
            }
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
            gMap.addMarker(
                MarkerOptions().position(location).title("Test Map")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            ).showInfoWindow()

            gMap.setOnMarkerClickListener { marker ->
                val detailPosko = PoskoDetailFragment.newInstance()
                val transaction = activity!!.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container, detailPosko)
                transaction.addToBackStack(null)
                transaction.commit()
                return@setOnMarkerClickListener true
            }
        }

    }

    companion object {
        fun newInstance(): PoskoMapFragment =
            PoskoMapFragment()
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            permissionId
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        setMaps("-6.3302658", "106.8388629")
    }
}
