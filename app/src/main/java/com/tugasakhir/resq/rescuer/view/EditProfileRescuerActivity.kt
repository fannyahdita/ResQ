package com.tugasakhir.resq.rescuer.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.model.Rescuer
import kotlinx.android.synthetic.main.activity_edit_profile_rescuer.*

class EditProfileRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var ref: DatabaseReference
    private lateinit var photoURI: Uri
    private var photoURL = ""
    private var uid = ""
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_rescuer)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.edit_profil_actionbar)
        actionBar.elevation = 0F

        uid = FirebaseAuth.getInstance().currentUser!!.uid

        imageview_foto_placer.setImageResource(R.drawable.ic_empty_pict)
        getRescuerData()
        button_save_profile.setOnClickListener {
            progressbar_edit_profile_rescuer.visibility = View.VISIBLE
            button_save_profile.isEnabled = false
            if (button_change_photo.visibility == View.VISIBLE) {
                uploadImage()
            } else {
                writeProfile()
            }
        }

        button_edit_photo.setOnClickListener {
            pickImageFromGallery()
            button_change_photo.visibility = View.VISIBLE
            button_edit_photo.visibility = View.GONE
        }

        button_change_photo.setOnClickListener {
            pickImageFromGallery()
        }

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            photoURI = data?.data!!
            imageview_foto_placer.setImageURI(data.data)
        }
    }

    private fun uploadImage() {
        val idPhoto = "rescuerPhotoProfile/$uid.${System.currentTimeMillis()}"
        val ref = FirebaseStorage.getInstance().reference.child(idPhoto)

        ref.putFile(photoURI).addOnSuccessListener {
            Toast.makeText(this, "Upload Image Succeeded", Toast.LENGTH_SHORT).show()
            ref.downloadUrl.addOnSuccessListener { uri ->
                photoURL = uri.toString()
                writeProfile(photoURL)
            }
        }.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun writeProfile(url: String) {
        val name = edittext_edit_name_rescuer.text.toString().trim()
        val phone = edittext_edit_phone_rescuer.text.toString().trim()

        FirebaseDatabase.getInstance().getReference("Rescuers")
            .child(uid)
            .child("name")
            .setValue(name)

        FirebaseDatabase.getInstance().getReference("Rescuers")
            .child(uid)
            .child("phone")
            .setValue(phone)

        FirebaseDatabase.getInstance().getReference("Rescuers")
            .child(uid)
            .child("profilePhoto")
            .setValue(url)

        progressbar_edit_profile_rescuer.visibility = View.GONE
        Toast.makeText(this, getString(R.string.toast_profile_changed), Toast.LENGTH_LONG).show()
        finish()
    }

    private fun writeProfile() {
        val name = edittext_edit_name_rescuer.text.toString().trim()
        val phone = edittext_edit_phone_rescuer.text.toString().trim()

        FirebaseDatabase.getInstance().getReference("Rescuers")
            .child(uid)
            .child("name")
            .setValue(name)

        FirebaseDatabase.getInstance().getReference("Rescuers")
            .child(uid)
            .child("phone")
            .setValue(phone)

        progressbar_edit_profile_rescuer.visibility = View.GONE
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
                edittext_id_rescuer.setText(rescuer?.employeeID)
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
