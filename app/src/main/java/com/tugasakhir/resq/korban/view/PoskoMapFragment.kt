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
import android.location.Location
import android.util.Log
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
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
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class PoskoMapFragment : Fragment(){

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private val permissionId = 42

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>

    private val poskoAdapter = PoskoAdapter()
    private lateinit var currentPosko: Posko
    private var role = ""
    val listPosko: ArrayList<Posko?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!
        role = arguments?.getString(ROLE).toString()
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
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
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

    private fun setMaps(lat: String, long: String, poskoId: String?) {
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
                FirebaseDatabase.getInstance().reference.child("Posko/${marker.title}")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            val currPosko = p0.getValue(Posko::class.java)
                            val intent = Intent(activity, PoskoDetailActivity::class.java)
                            intent.putExtra("EXTRA_POSKO", currPosko as Serializable)
                            intent.putExtra("EXTRA_LAT", lat)
                            intent.putExtra("EXTRA_LONG", long)
                            activity?.startActivity(intent)
                        }


                        override fun onCancelled(p0: DatabaseError) {
                            Log.d("TemukanSayaError : ", p0.message)
                        }
                    })

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
                        val open = posko.child("open").value.toString().toBoolean()
                        currentPosko = posko.getValue(Posko::class.java)!!
                        if(open) {
                            listPosko.add(currentPosko)
                            setMaps(currentPosko.latitude.toString(), currentPosko.longitude.toString(), posko.key)
                        }

                    }
                    compareLoc(listPosko, lat, long)
                    poskoAdapter.setPosko(listPosko, lat, long)
                }


                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })

    }

    private fun compareLoc(posko: ArrayList<Posko?>, currentLat: String?, currentLong: String?) : ArrayList<Posko?> {
        val comp = object : Comparator<Posko?> {
            override fun compare(posko1: Posko?, posko2: Posko?): Int {
                val results1 = FloatArray(1)
                Location.distanceBetween(currentLat!!.toDouble(), currentLong!!.toDouble(), posko1?.latitude!!.toDouble(), posko1.longitude.toDouble(), results1)
                val distance1 = results1[0]

                val results2 = FloatArray(1)
                Location.distanceBetween(currentLat.toDouble(), currentLong.toDouble(), posko2?.latitude!!.toDouble(), posko2.longitude.toDouble(), results2)
                val distance2 = results2[0]

                return distance1.compareTo(distance2)
            }

        }

        Collections.sort(posko, comp)
        return posko
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
