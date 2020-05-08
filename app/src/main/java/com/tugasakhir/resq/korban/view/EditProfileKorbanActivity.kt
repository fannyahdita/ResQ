package com.tugasakhir.resq.korban.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.tugasakhir.resq.korban.model.AkunKorban
import com.tugasakhir.resq.base.helper.ImageAdjustment
import kotlinx.android.synthetic.main.activity_edit_profile_korban.*
import java.io.ByteArrayOutputStream
import java.io.IOException

private const val IMAGE_PICK_CODE = 1000
private const val PERMISSION_CODE = 1001

class EditProfileKorbanActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var ref: DatabaseReference
    private lateinit var photoURI: Uri
    private var data = Intent()
    private var uid = ""
    private var photoProfile = ""
    private var photoURL = ""
    private var resultCode = 0
    private lateinit var rotatedBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_korban)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.edit_profil_actionbar)
        actionBar.elevation = 0F

        uid = FirebaseAuth.getInstance().currentUser!!.uid
        imageview_foto_placer_korban.setImageResource(R.drawable.ic_empty_pict)
        getKorbanData()

        button_edit_photo_korban.setOnClickListener {
            if (checkPermissions()) {
                pickImageFromGallery()
                button_edit_photo_korban.visibility = View.GONE
                button_change_photo_korban.visibility = View.VISIBLE
            } else {
                requestPermissions()
            }
        }

        button_change_photo_korban.setOnClickListener {
            pickImageFromGallery()
        }

        button_save_profile_korban.setOnClickListener {
            progressbar_edit_profile_korban.visibility = View.VISIBLE
            button_save_profile_korban.isEnabled = false
            if (button_change_photo_korban.visibility == View.VISIBLE) {
                uploadImage(rotatedBitmap)
            } else {
                writeProfile()
            }
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
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    this.applicationContext.contentResolver,
                    photoURI
                )
                rotatedBitmap = ImageAdjustment.rotateImageIfRequired(this, bitmap, photoURI)
                imageview_foto_placer_korban.setImageBitmap(rotatedBitmap)
                this.data = data
                this.resultCode = resultCode
            } catch (e: IOException) {
                e.printStackTrace()
            }
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

                if (akunKorban?.profilePhoto == "") {
                    imageview_foto_placer_korban.setImageResource(R.drawable.ic_empty_pict)
                    photoProfile = ""
                } else {
                    photoProfile = akunKorban?.profilePhoto.toString()
                    Picasso.get()
                        .load(akunKorban?.profilePhoto)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.ic_empty_pict)
                        .error(R.drawable.ic_empty_pict)
                        .into(imageview_foto_placer_korban)
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("EditProfileVictim : ", p0.message)
            }
        }

        ref.addListenerForSingleValueEvent(akunKorbanFragment)
    }

    private fun uploadImage(bmp: Bitmap) {
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val data = stream.toByteArray()

        val idPhoto = "victimPhotoProfile/$uid.${System.currentTimeMillis()}"
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
            Toast.makeText(this, "Upload Image Succeeded", Toast.LENGTH_SHORT).show()
            ref.downloadUrl.addOnSuccessListener { uri ->
                photoURL = uri.toString()
                writeProfile(photoURL)
            }
        }.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }

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

    private fun writeProfile(url: String) {
        val name = edittext_edit_name_korban.text.toString().trim()

        FirebaseDatabase.getInstance().getReference("AkunKorban")
            .child(uid)
            .child("name")
            .setValue(name)

        FirebaseDatabase.getInstance().getReference("AkunKorban")
            .child(uid)
            .child("profilePhoto")
            .setValue(url)

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