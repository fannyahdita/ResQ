package com.tugasakhir.resq.base.view

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.R
import com.tugasakhir.resq.base.adapter.WaterLevelAdapter
import com.tugasakhir.resq.base.model.WaterGate
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private var role = ""
    private var currLat = ""
    private var currLong = ""
    private var waterGatesList: ArrayList<WaterGate?> = ArrayList()
    private var waterGatesClosest: ArrayList<WaterGate?> = ArrayList()
    private var adapter = WaterLevelAdapter()

    companion object {

        const val ROLE = ""
        fun newInstance(isKorban: Boolean, lat: String, long: String): HomeFragment {
            val fragment = HomeFragment()
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        role = arguments?.getString(ROLE).toString()
        currLat = arguments?.getString("lat").toString()
        currLong = arguments?.getString("long").toString()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (role == "victim") {
            FirebaseDatabase.getInstance().getReference("AkunKorban/${uid}/name")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        textview_greetings.text =
                            getString(R.string.greetings, p0.value)
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("HomeError", p0.message)
                    }
                })
        } else {
            FirebaseDatabase.getInstance().getReference("Rescuers/${uid}/name")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        textview_greetings.text =
                            getString(R.string.greetings, p0.value)
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("HomeError", p0.message)
                    }
                })

        }

        addWaterGateList()
        recyclerview_water_level.adapter = adapter
        recyclerview_water_level.layoutManager = LinearLayoutManager(activity)

        textview_see_more.setOnClickListener {
            val finalList = compareLoc(waterGatesList, currLat, currLong)
            adapter.notifyItemRangeRemoved(0,5)
            adapter.setWaterGates(finalList, currLat.toDouble(), currLong.toDouble())
            adapter.notifyDataSetChanged()
            textview_see_more.visibility = View.GONE
        }

        button_buku_saku.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://siaga.bnpb.go.id/hkb/po-content/uploads/documents/Buku_Saku-10Jan18_FA.pdf"))
            startActivity(intent)
        }
    }

    private fun addWaterGateList() {
        try {
            val jsonArray = JSONObject(loadJson()!!).getJSONArray("watergates")

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("nama_pintu_air")
                val location = jsonObject.getString("lokasi")
                val latitude = jsonObject.getDouble("latitude")
                val longitude = jsonObject.getDouble("longitude")
                val datetime = jsonObject.getString("tanggal")
                val level = jsonObject.getLong("tinggi_air")
                val status = jsonObject.getString("status_siaga")

                val waterGate = WaterGate(
                    name,
                    location,
                    latitude,
                    longitude,
                    datetime,
                    level,
                    status
                )
                waterGatesList.add(waterGate)
            }

            addTop5List()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun addTop5List() {
        val finalList = compareLoc(waterGatesList, currLat, currLong)
        for(i in 0 until 5) {
            waterGatesClosest.add(finalList[i])
        }

        adapter.setWaterGates(waterGatesClosest, currLat.toDouble(), currLong.toDouble())
    }

    private fun loadJson(): String? {
        val json: String

        try {
            val inputStream: InputStream =
                activity?.assets!!.open("data-tinggi-muka-air-mei-2020.json")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (x: IOException) {
            x.printStackTrace()
            return null
        }

        return json
    }

    private fun compareLoc(waterGates: ArrayList<WaterGate?>, currentLat: String?, currentLong: String?) : ArrayList<WaterGate?> {
        val comp = object : Comparator<WaterGate?> {
            override fun compare(waterGate1: WaterGate?, waterGate2: WaterGate?): Int {
                val results1 = FloatArray(1)
                if (waterGate1 != null && waterGate2 != null) {
                    Location.distanceBetween(currentLat!!.toDouble(), currentLong!!.toDouble(), waterGate1.latitude, waterGate1.longitude, results1)
                    val distance1 = results1[0]

                    val results2 = FloatArray(1)
                    Location.distanceBetween(currentLat.toDouble(), currentLong.toDouble(), waterGate2.latitude, waterGate2.longitude, results2)
                    val distance2 = results2[0]
                    return distance1.compareTo(distance2)
                }
                return 0
            }

        }
        Collections.sort(waterGates, comp)
        return waterGates
    }
}
