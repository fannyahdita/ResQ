package com.tugasakhir.resq.rescuer.view

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.helper.VictimInfoData
import com.tugasakhir.resq.rescuer.model.Posko
import kotlinx.android.synthetic.main.activity_add_posko_rescuer.*
import java.util.*

class AddPoskoRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var victimInfoData: VictimInfoData
    private lateinit var posko: Posko
    private var idRescuer: String = ""
    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_posko_rescuer)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.add_posko_actionbar)
        actionBar.elevation = 0F

        victimInfoData = VictimInfoData()
        idRescuer = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val location = intent.getStringExtra("location")?.split(" ")
        latitude = location!![0].toDouble()
        longitude = location[1].toDouble()

        edittext_edit_posko_address_generate.setText(victimInfoData.getAddress(latitude!!, longitude!!, this))

        button_add_posko_finish.setOnClickListener {
            validatePosko()
        }
    }

    private fun validatePosko() {
        val poskoName = edittext_edit_posko_name.text.toString().trim()
        val city = edittext_edit_posko_city.text.toString().trim()
        val district = edittext_edit_posko_district.text.toString().trim()
        val subDistrict = edittext_edit_posko_sub_district.text.toString().trim()
        val mapAddress = edittext_edit_posko_address_generate.text.toString().trim()
        val notesAddress = edittext_edit_posko_address_notes.text.toString().trim()
        val capacity = edittext_edit_posko_capacity.text.toString().trim()
        val contactName = edittext_edit_posko_contact_name.text.toString().trim()
        val contactNumber = edittext_edit_posko_contact_number.text.toString().trim()
        var hasMedic = false
        var hasBed = false
        var hasKitchen = false
        var hasLogistic = false
        var hasWC = false

        if (poskoName.isEmpty()) {
            edittext_edit_posko_name.error = getString(R.string.field_is_empty)
            edittext_edit_posko_name.requestFocus()
            return
        }

        if (city.isEmpty()) {
            edittext_edit_posko_city.error = getString(R.string.field_is_empty)
            edittext_edit_posko_city.requestFocus()
            return
        }

        if (district.isEmpty()) {
            edittext_edit_posko_district.error = getString(R.string.field_is_empty)
            edittext_edit_posko_district.requestFocus()
            return
        }

        if (subDistrict.isEmpty()) {
            edittext_edit_posko_sub_district.error = getString(R.string.field_is_empty)
            edittext_edit_posko_sub_district.requestFocus()
            return
        }

        if (capacity.isEmpty()) {
            edittext_edit_posko_capacity.error = getString(R.string.field_is_empty)
            edittext_edit_posko_capacity.requestFocus()
            return
        }

        if (contactName.isEmpty()) {
            edittext_edit_posko_contact_name.error = getString(R.string.field_is_empty)
            edittext_edit_posko_contact_name.requestFocus()
            return
        }

        if (contactNumber.isEmpty()) {
            edittext_edit_posko_contact_number.error = getString(R.string.field_is_empty)
            edittext_edit_posko_contact_number.requestFocus()
            return
        }

        if (checkbox_medic.isChecked) {
            hasMedic = true
        }

        if (checkbox_bed.isChecked) {
            hasBed = true
        }

        if (checkbox_kitchen.isChecked) {
            hasKitchen = true
        }

        if (checkbox_logistic.isChecked) {
            hasLogistic = true
        }

        if (checkbox_wc.isChecked) {
            hasWC = true
        }

        val date = victimInfoData.getCurrentDateTime().toString("dd/MM/yyy HH:mm")

        posko = Posko(
            idRescuer, latitude!!, longitude!!, poskoName, city, district, subDistrict,
            mapAddress, notesAddress, capacity.toLong(), hasMedic, hasKitchen, hasWC, hasLogistic, hasBed, date,
            contactName, contactNumber, true
        )

        addPosko(posko)
    }

    private fun addPosko(posko: Posko) {
        val ref = FirebaseDatabase.getInstance().getReference("Posko")
        val idPosko = ref.push().key.toString()

        ref.child(idPosko).setValue(posko).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Log.d("AddNewPosko: ", it.exception?.message!!)
            }
        }
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocode = Geocoder(this, Locale.getDefault())
        val address = geocode.getFromLocation(latitude, longitude, 1)
        return address[0].getAddressLine(0)
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
}

private fun Date.toString(s: String, locale: Locale = Locale.getDefault()): String {
    val formatter = java.text.SimpleDateFormat(s, locale)
    return formatter.format(this)
}