package com.tugasakhir.resq.rescuer.view

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.model.Rescuer
import kotlinx.android.synthetic.main.activity_edit_profile_rescuer.*

class EditProfileRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_rescuer)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.edit_profil_actionbar)
        actionBar.elevation = 0F

        imageview_foto_placer.setImageResource(R.drawable.ic_empty_pict)
        getRescuerData()
        button_save_profile.setOnClickListener {
            writeProfile()
        }
    }

    private fun writeProfile() {
        val name = edittext_edit_name_rescuer.text.toString().trim()
        val phone = edittext_edit_phone_rescuer.text.toString().trim()

        FirebaseDatabase.getInstance().getReference("Rescuers")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("name")
            .setValue(name)

        FirebaseDatabase.getInstance().getReference("Rescuers")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("phone")
            .setValue(phone)

        Toast.makeText(this, getString(R.string.toast_profile_changed), Toast.LENGTH_LONG).show()
        finish()
    }

    private fun getRescuerData() {
        val user = FirebaseAuth.getInstance().currentUser!!
        ref = FirebaseDatabase.getInstance().getReference("Rescuers").child(user.uid)
        val rescuerFragment = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val rescuer = p0.getValue(Rescuer::class.java)
                edittext_edit_name_rescuer.setText(rescuer?.name)
                edittext_edit_phone_rescuer.setText(rescuer?.phone)
                edittext_edit_email_rescuer.setText(rescuer?.email)
                edittext_edit_instansi_rescuer.setText(rescuer?.instansi)
                edittext_edit_division_rescuer.setText(rescuer?.division)
                edittext_id_division_rescuer.setText(rescuer?.employeeID)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("EditProfileRescuer : ", p0.message)
            }
        }

        setPreviousData(ref, rescuerFragment)
    }

    private fun setPreviousData(ref: DatabaseReference, profile: ValueEventListener) {
        ref.addListenerForSingleValueEvent(profile)
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
