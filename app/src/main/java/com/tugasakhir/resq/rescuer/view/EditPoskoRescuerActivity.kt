package com.tugasakhir.resq.rescuer.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.tugasakhir.resq.R
import com.tugasakhir.resq.base.model.Posko
import kotlinx.android.synthetic.main.activity_edit_posko_rescuer.*
import java.io.Serializable

class EditPoskoRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_posko_rescuer)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.edit_posko_actionbar)
        actionBar.elevation = 0F

        val posko = intent.extras?.get("EXTRA_POSKO") as Posko

        setData(posko)

        textview_max_char_name.text =
            getString(R.string.max_char_50, edittext_edit_posko_name.text.length)
        edittext_edit_posko_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textview_max_char_name.text = getString(R.string.max_char_50, p0?.length)
            }
        })

        textview_max_char_info.text =
            getString(R.string.max_char_280, edittext_edit_posko_additional_info.text.length)
        edittext_edit_posko_additional_info.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textview_max_char_info.text = getString(R.string.max_char_280, p0?.length)
            }
        })

        button_edit_posko_finish.setOnClickListener {
            validatePosko(posko)
        }
    }

    private fun setData(posko: Posko) {
        edittext_edit_posko_name.setText(posko.poskoName)
        edittext_edit_posko_address_generate.setText(posko.mapAddress)
        edittext_edit_posko_address_notes.setText(posko.noteAddress)
        edittext_edit_posko_capacity.setText(posko.capacity.toString())
        checkbox_medic.isChecked = posko.hasMedic
        checkbox_bed.isChecked = posko.hasBed
        checkbox_kitchen.isChecked = posko.hasKitchen
        checkbox_logistic.isChecked = posko.hasLogistic
        checkbox_wc.isChecked = posko.hasWC
        edittext_edit_posko_additional_info.setText(posko.additionalInfo)
        edittext_edit_posko_contact_name.setText(posko.contactName)
        edittext_edit_posko_contact_number.setText(posko.contactNumber.substring(3))
    }

    private fun validatePosko(posko: Posko) {
        val poskoName = edittext_edit_posko_name.text.toString().trim()
        val mapAddress = edittext_edit_posko_address_generate.text.toString().trim()
        val notesAddress = edittext_edit_posko_address_notes.text.toString().trim()
        val capacity = edittext_edit_posko_capacity.text.toString().trim()
        val contactName = edittext_edit_posko_contact_name.text.toString().trim()
        val contactNumber = edittext_edit_posko_contact_number.text.toString().trim()
        val additionalInfo = edittext_edit_posko_additional_info.text.toString().trim()
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

        if (contactNumber.length !in 14 downTo 7) {
            edittext_edit_posko_contact_number.error = getString(R.string.phone_is_not_valid)
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

        val newPosko = Posko(
            posko.id,
            posko.idRescuer,
            posko.latitude,
            posko.longitude,
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
            posko.createdAt,
            contactName,
            "+62$contactNumber",
            true
        )

        editPoskoToFirebase(newPosko)
    }

    private fun editPoskoToFirebase(posko: Posko) {
        FirebaseDatabase.getInstance().getReference("Posko/${posko.id}")
            .setValue(posko)

        val intent = Intent()
        intent.putExtra("NEW_POSKO", posko as Serializable)
        setResult(Activity.RESULT_OK, intent)
        finish()
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