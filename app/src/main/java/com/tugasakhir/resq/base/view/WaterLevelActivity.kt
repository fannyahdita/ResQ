package com.tugasakhir.resq.base.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasakhir.resq.R
import com.tugasakhir.resq.base.adapter.WaterLevelAdapter
import com.tugasakhir.resq.base.model.WaterGate
import kotlinx.android.synthetic.main.activity_water_level.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class WaterLevelActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private var waterGatesList: ArrayList<WaterGate> = ArrayList()
    private var adapter = WaterLevelAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_level)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = title
        actionBar.elevation = 0F

        addToList()
        recyclerview_water_level.adapter = adapter
        recyclerview_water_level.layoutManager = LinearLayoutManager(this)
    }

    private fun addToList() {
        try {
            val jsonArray = JSONObject(loadJson()!!).getJSONArray("watergates")


            for (i in 0 until jsonArray.length()) {
                Toast.makeText(this, "Setelah For", Toast.LENGTH_SHORT).show()
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

            adapter.setWaterGates(waterGatesList)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun loadJson(): String? {
        var json = ""

        try {
            val inputStream: InputStream = assets.open("data-tinggi-muka-air-mei-2020.json")
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

}