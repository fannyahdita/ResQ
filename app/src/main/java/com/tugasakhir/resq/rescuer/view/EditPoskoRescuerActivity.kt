package com.tugasakhir.resq.rescuer.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.model.Posko
import kotlinx.android.synthetic.main.activity_edit_posko_rescuer.*

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
        edittext_edit_posko_contact_number.setText(posko.contactNumber)
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