package com.tugasakhir.resq.rescuer.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
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

        edittext_add_posko_address_generate.setText(
            victimInfoData.getAddress(
                latitude!!,
                longitude!!,
                this
            )
        )

        textview_max_char_name.text = getString(R.string.max_char_50, 0)
        edittext_add_posko_name.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textview_max_char_name.text = getString(R.string.max_char_50, p0?.length)
            }

            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        textview_max_char_info.text = getString(R.string.max_char_280, 0)
        edittext_add_posko_additional_info.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textview_max_char_info.text = getString(R.string.max_char_280, p0?.length)
            }

            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        button_add_posko_finish.setOnClickListener {
            validatePosko()
        }
    }

    private fun validatePosko() {
        val poskoName = edittext_add_posko_name.text.toString().trim()
        val mapAddress = edittext_add_posko_address_generate.text.toString().trim()
        val notesAddress = edittext_add_posko_address_notes.text.toString().trim()
        val capacity = edittext_add_posko_capacity.text.toString().trim()
        val contactName = edittext_add_posko_contact_name.text.toString().trim()
        val contactNumber = edittext_add_posko_contact_number.text.toString().trim()
        val additionalInfo = edittext_add_posko_additional_info.text.toString().trim()
        var hasMedic = false
        var hasBed = false
        var hasKitchen = false
        var hasLogistic = false
        var hasWC = false

        if (poskoName.isEmpty()) {
            edittext_add_posko_name.error = getString(R.string.field_is_empty)
            edittext_add_posko_name.requestFocus()
            return
        }

        if (capacity.isEmpty()) {
            edittext_add_posko_capacity.error = getString(R.string.field_is_empty)
            edittext_add_posko_capacity.requestFocus()
            return
        }

        if (contactName.isEmpty()) {
            edittext_add_posko_contact_name.error = getString(R.string.field_is_empty)
            edittext_add_posko_contact_name.requestFocus()
            return
        }

        if (contactNumber.isEmpty()) {
            edittext_add_posko_contact_number.error = getString(R.string.field_is_empty)
            edittext_add_posko_contact_number.requestFocus()
            return
        }

        if (contactNumber.length in 14 downTo 7) {
            edittext_add_posko_contact_number.error = getString(R.string.phone_is_not_valid)
            edittext_add_posko_contact_number.requestFocus()
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


        val ref = FirebaseDatabase.getInstance().getReference("Posko")
        val idPosko = ref.push().key.toString()

        posko = Posko(
            idPosko,
            idRescuer,
            latitude!!,
            longitude!!,
            poskoName,
            mapAddress,
            notesAddress,
            capacity.toLong(),
            hasMedic,
            hasKitchen,
            hasWC,
            hasLogistic,
            hasBed,
            additionalInfo,
            date,
            contactName,
            "+62$contactNumber",
            true
        )

        addPosko(ref, posko)
    }

    private fun addPosko(ref: DatabaseReference, posko: Posko) {

        ref.child(posko.id).setValue(posko).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Log.d("AddNewPosko: ", it.exception?.message!!)
            }
        }
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