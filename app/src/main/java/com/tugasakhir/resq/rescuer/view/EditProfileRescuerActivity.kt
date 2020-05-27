package com.tugasakhir.resq.rescuer.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.tugasakhir.resq.R
import com.tugasakhir.resq.base.helper.ImageAdjustment
import com.tugasakhir.resq.rescuer.model.Rescuer
import kotlinx.android.synthetic.main.activity_edit_profile_rescuer.*
import java.io.ByteArrayOutputStream
import java.io.IOException

private const val IMAGE_PICK_CODE = 1000
private const val PERMISSION_CODE = 1001

class EditProfileRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var ref: DatabaseReference
    private var photoProfile = ""
    private lateinit var photoURI: Uri
    private var photoURL = ""
    private var uid = ""
    private var data = Intent()
    private var resultCode = 0
    private lateinit var rotatedBitmap: Bitmap

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
            if (validateForm()) {
                progressbar_edit_profile_rescuer.visibility = View.VISIBLE
                button_save_profile.isEnabled = false
                if (button_change_photo.visibility == View.VISIBLE) {
                    uploadImage(rotatedBitmap)
                } else {
                    writeProfile()
                }
            }
        }

        button_edit_photo.setOnClickListener {
            if (checkPermissions()) {
                pickImageFromGallery()
                button_change_photo.visibility = View.VISIBLE
                button_edit_photo.visibility = View.GONE
            } else {
                requestPermissions()
            }
        }

        button_change_photo.setOnClickListener {
            pickImageFromGallery()
        }

        textview_max_char_name.text =
            getString(R.string.max_char_50, edittext_edit_name_rescuer.text.length)
        edittext_edit_name_rescuer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textview_max_char_name.text = getString(R.string.max_char_50, p0?.length!!)
            }
        })

    }

    private fun validateForm(): Boolean {
        if (edittext_edit_name_rescuer.text.isEmpty()) {
            edittext_edit_name_rescuer.error = getString(R.string.field_is_empty)
            edittext_edit_name_rescuer.requestFocus()
            return false
        }

        if (edittext_edit_phone_rescuer.text.isEmpty()) {
            edittext_edit_phone_rescuer.error = getString(R.string.field_is_empty)
            edittext_edit_phone_rescuer.requestFocus()
            return false
        }

        if (edittext_edit_phone_rescuer.text.length !in 16 downTo 9) {
            edittext_edit_phone_rescuer.error = getString(R.string.phone_is_not_valid)
            edittext_edit_phone_rescuer.requestFocus()
            return false
        }
        return true
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            photoURI = data?.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    this.applicationContext.contentResolver,
                    photoURI
                )
                rotatedBitmap = ImageAdjustment.rotateImageIfRequired(this, bitmap, photoURI)
                imageview_foto_placer.setImageBitmap(rotatedBitmap)
                this.data = data
                this.resultCode = resultCode
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(bmp: Bitmap) {
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val data = stream.toByteArray()

        val idPhoto = "rescuerPhotoProfile/$uid.${System.currentTimeMillis()}"
        val ref = FirebaseStorage.getInstance().reference.child(idPhoto)

        if (photoProfile != "") {
            FirebaseStorage.getInstance().getReferenceFromUrl(photoProfile).delete()
                .addOnSuccessListener {
                    Log.d("Delete Storage", "Succeed")
                }
                .addOnFailureListener {
                    Log.d("Delete Storage", "Failed")
                }
        }

        ref.putBytes(data).addOnSuccessListener {
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
        val phone = "+62${edittext_edit_phone_rescuer.text.toString().trim()}"

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
        val phone = "+62${edittext_edit_phone_rescuer.text.toString().trim()}"

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
                edittext_edit_phone_rescuer.setText(rescuer?.phone?.substring(3))
                edittext_edit_email_rescuer.setText(rescuer?.email)
                edittext_edit_instansi_rescuer.setText(rescuer?.instansi)
                edittext_edit_division_rescuer.setText(rescuer?.division)
                edittext_id_rescuer.setText(rescuer?.employeeID)

                if (rescuer?.profilePhoto == "") {
                    imageview_foto_placer.setImageResource(R.drawable.ic_empty_pict)
                    photoProfile = ""
                } else {
                    photoProfile = rescuer?.profilePhoto.toString()
                    Picasso.get()
                        .load(rescuer?.profilePhoto)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.ic_empty_pict)
                        .error(R.drawable.ic_empty_pict)
                        .into(imageview_foto_placer)
                }

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

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_CODE
        )
    }
}

