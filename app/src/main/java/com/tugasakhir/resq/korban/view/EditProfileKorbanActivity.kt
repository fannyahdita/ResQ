package com.tugasakhir.resq.korban.view

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.AkunKorban
import kotlinx.android.synthetic.main.activity_edit_profile_korban.*
import kotlinx.android.synthetic.main.activity_edit_profile_korban.button_save_profile
import kotlinx.android.synthetic.main.activity_edit_profile_korban.imageview_foto_placer

class EditProfileKorbanActivity: AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_korban)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.edit_profil_actionbar)
        actionBar.elevation = 0F

        imageview_foto_placer.setImageResource(R.drawable.ic_empty_pict)
        getKorbanData()
        button_save_profile.setOnClickListener {
            writeProfile()
        }
    }

    private fun getKorbanData() {
        val user = FirebaseAuth.getInstance().currentUser!!
        ref = FirebaseDatabase.getInstance().getReference("AkunKorban").child(user.uid)
        val akunKorbanFragment = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val akunKorban = p0.getValue(AkunKorban::class.java)
                edittext_edit_name_korban.setText(akunKorban?.name)
                edittext_edit_phone_korban.setText(akunKorban?.phone)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("EditProfileRescuer : ", p0.message)
            }
        }

        ref.addListenerForSingleValueEvent(akunKorbanFragment)
    }

    private fun writeProfile() {
        val name = edittext_edit_name_korban.text.toString().trim()

        FirebaseDatabase.getInstance().getReference("AkunKorban")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("name")
            .setValue(name)

        Toast.makeText(this, getString(R.string.toast_profile_changed), Toast.LENGTH_LONG).show()
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