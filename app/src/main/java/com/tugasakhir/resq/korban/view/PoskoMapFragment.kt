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
import android.util.Log
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.korban.PoskoAdapter
import com.tugasakhir.resq.rescuer.model.Posko
import com.tugasakhir.resq.rescuer.view.AddPoskoLocationActivity
import kotlinx.android.synthetic.main.fragment_list_posko.*
import java.io.Serializable

class PoskoMapFragment : Fragment(){

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private val permissionId = 42

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>

    private val poskoAdapter = PoskoAdapter()
    private lateinit var currentPosko: Posko
    private var role = ""

    private var TAG = "LIST POSKO "
    val listPosko: ArrayList<Posko?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!
        role = arguments?.getString(PoskoListFragment.ROLE).toString()
        return inflater.inflate(R.layout.fragment_posko_korban, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.fragment_map_posko) as SupportMapFragment

        posko_recycler_view.layoutManager = LinearLayoutManager(activity)
        posko_recycler_view.adapter = poskoAdapter

        val currentLat = arguments!!.getString("lat")
        val currentLong = arguments!!.getString("long")
        setCurrentLoc(currentLat, currentLong)

        bottomSheetBehavior = BottomSheetBehavior.from<RelativeLayout>(fragment_list_posko)
        setBottomSheetList()

        fetchPoskoData(currentLat, currentLong)


    }

    private fun setBottomSheetList() {
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }
        })
    }

    private fun setCurrentLoc(currentLat: String?, currentLong: String?) {
        mapFragment.getMapAsync { googleMap ->
            val currentLoc =  LatLng(currentLat!!.toDouble(), currentLong!!.toDouble())
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 14f))
        }
    }

    private fun setMaps(lat: String, long: String, poskoId: Int) {
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
            gMap.addMarker(
                MarkerOptions().position(location).title(poskoId.toString())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )

            gMap.setOnMarkerClickListener { marker ->
                val intent = Intent(activity, PoskoDetailActivity::class.java)
                intent.putExtra("EXTRA_POSKO", listPosko[marker.title.toInt()-1] as Serializable)
                intent.putExtra("EXTRA_LAT", lat)
                intent.putExtra("EXTRA_LONG", long)
                activity?.startActivity(intent)
                return@setOnMarkerClickListener true
            }
        }

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

    override fun onResume() {
        super.onResume()
        if (role == "rescuer") {
            textview_add_posko_rescuer.visibility = View.VISIBLE
        }
        textview_add_posko_rescuer.setOnClickListener {
            val intent = Intent(activity, AddPoskoLocationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchPoskoData(lat: String?, long: String?) {
        FirebaseDatabase.getInstance().getReference("Posko")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val children = p0.children
                    children.forEach { posko ->
                        val mapAddress = posko.child("mapAddress").value.toString()
                        val notesAddress = posko.child("noteAddress").value.toString()
                        val capacity = posko.child("capacity").value.toString().toLong()
                        val city = posko.child("city").value.toString()
                        val contactName = posko.child("contactName").value.toString()
                        val contactNumber = posko.child("contactNumber").value.toString()
                        val createdAt = posko.child("createdAt").value.toString()
                        val district = posko.child("district").value.toString()
                        val hasBed = posko.child("hasBed").value.toString().toBoolean()
                        val hasKitchen = posko.child("hasKitchen").value.toString().toBoolean()
                        val hasLogistic = posko.child("hasLogistic").value.toString().toBoolean()
                        val hasMedic = posko.child("hasMedic").value.toString().toBoolean()
                        val hasWC = posko.child("hasWC").value.toString().toBoolean()
                        val idRescuer = posko.child("idRescuer").value.toString()
                        val latitude = posko.child("latitude").value.toString().toDouble()
                        val longitude = posko.child("longitude").value.toString().toDouble()
                        val open = posko.child("open").value.toString().toBoolean()
                        val poskoName = posko.child("poskoName").value.toString()
                        val subDistrict = posko.child("subDistrict").value.toString()

                        currentPosko = Posko(
                            idRescuer,
                            latitude,
                            longitude,
                            poskoName,
                            city,
                            district,
                            subDistrict,
                            mapAddress,
                            notesAddress,
                            capacity,
                            hasMedic,
                            hasKitchen,
                            hasWC,
                            hasLogistic,
                            hasBed,
                            createdAt,
                            contactName,
                            contactNumber,
                            open
                        )

                        listPosko.add(currentPosko)
                        setMaps(latitude.toString(), longitude.toString(), listPosko.size)

                    }
                    poskoAdapter.setPosko(listPosko, lat, long)
                }


                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })

    }

    companion object {
        const val ROLE = ""
        fun newInstance(isKorban: Boolean, lat: String, long: String): PoskoMapFragment {
            val fragment = PoskoMapFragment()
            val bundle = Bundle()
            bundle.putString("lat", lat)
            bundle.putString("long", long)
            if (isKorban) {
                bundle.putString(ROLE, "victim")
            } else {
                bundle.putString(ROLE, "rescuer")
            }
            fragment.arguments = bundle
            return fragment
        }
    }


}
