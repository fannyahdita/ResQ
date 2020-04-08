package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.PoskoAdapter
import com.tugasakhir.resq.rescuer.model.Posko
import com.tugasakhir.resq.rescuer.view.AddPoskoRescuerActivity
import kotlinx.android.synthetic.main.fragment_list_posko.*

class PoskoListFragment : Fragment() {

    private val poskoAdapter = PoskoAdapter()
    private lateinit var currentPosko: Posko
    private var role = ""

    companion object {
        const val ROLE = ""
        fun newInstance(isKorban: Boolean, lat: String, long: String): PoskoListFragment {
            val fragment = PoskoListFragment()
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

    private var TAG = "LIST POSKO "
    val listPosko: ArrayList<Posko?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        role = arguments?.getString(ROLE).toString()
        return inflater.inflate(R.layout.fragment_list_posko, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "MASUK INIT SEBAGAi $role")
        posko_recycler_view.layoutManager = LinearLayoutManager(activity)
        posko_recycler_view.adapter = poskoAdapter

        val lat = arguments!!.getString("lat")
        val long = arguments!!.getString("long")

        fetchPoskoData(lat, long)

    }

    override fun onResume() {
        super.onResume()
        if (role == "rescuer") {
            textview_add_posko_rescuer.visibility = View.VISIBLE
        }
    }

    private fun fetchPoskoData(lat: String?, long: String?) {
        FirebaseDatabase.getInstance().getReference("Posko")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val children = p0.children
                    children.forEach { posko ->
                        val address = posko.child("address").value.toString()
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
                            address,
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

                    }
                    poskoAdapter.setPosko(listPosko, lat, long)
                }


                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })

    }


}