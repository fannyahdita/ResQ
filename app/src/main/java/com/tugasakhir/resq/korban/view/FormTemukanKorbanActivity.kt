package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tugasakhir.resq.base.view.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.InfoKorban
import kotlinx.android.synthetic.main.activity_temukansayaform_korban.*
import kotlinx.android.synthetic.main.activity_temukansayaform_korban.progressbar_name

const val EXTRA_ID_INFOKORBAN = "com.tugasakhir.resq.korban.ID_INFOKORBAN"
const val EXTRA_PREV_ACTIVITY = "com.tugasakhir.resq.korban.ID_PREV_ACTIVITY"
const val EXTRA_LAT = "com.tugasakhir.resq.korban.LAT"
const val EXTRA_LONG = "com.tugasakhir.resq.korban.LONG"

class FormTemukanKorbanActivity : AppCompatActivity() {

    private lateinit var actionBar : ActionBar
    var lansia_displayInt : Int = 0
    var anak_displayInt : Int = 0
    var dewasa_displayInt : Int = 0
    var status: Int = 0
    var long: String = ""
    var lat: String = ""
    var isEvakuasi: Boolean = true
    var isMakanan: Boolean = false
    var isMedis: Boolean = false
    var idKorban: String = ""

    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var ref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temukansayaform_korban)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        firebaseAuth = FirebaseAuth.getInstance()

        ref = FirebaseDatabase.getInstance().getReference("InfoKorban")

        lat = intent.getStringExtra(EXTRA_LAT)
        long = intent.getStringExtra(EXTRA_LONG)

        getIdKorban()

        radio_jenisbantuan.setOnCheckedChangeListener { group, checkedId ->
            if (R.id.rb_evakuasi == checkedId) {
                isEvakuasi = true
                isMedis = false
                isMakanan = false
            } else if (R.id.rb_makanan == checkedId) {
                isMakanan = true
                isEvakuasi = false
                isMedis = false
            } else if (R.id.rb_medis == checkedId) {
                isMedis = true
                isEvakuasi = false
                isMakanan = false
            }
        }

        button_send.setOnClickListener {
            if (integer_number_lansia.text.toString().trim() == "0"
                && integer_number_dewasa.text.toString().trim() == "0"
                && integer_number_anak.text.toString().trim() == "0") {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Gagal mengirimkan data")
                builder.setMessage("Pastikan kamu memasukkan jumlah korban")

                builder.setPositiveButton("oke"){_,_ ->

                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            } else {
                getDataKorban()
            }
        }

        decrease_lansia.setOnClickListener {
            if (lansia_displayInt != 0) {
                lansia_displayInt--
            }
            status = 1
            displayNumber(lansia_displayInt)
        }

        decrease_anak.setOnClickListener {
            if (anak_displayInt != 0) {
                anak_displayInt--
            }
            status = 3
            displayNumber(anak_displayInt)
        }

        decrease_dewasa.setOnClickListener {
            if (dewasa_displayInt != 0) {
                dewasa_displayInt--
            }
            status = 2
            displayNumber(dewasa_displayInt)
        }

        increase_lansia.setOnClickListener {
            lansia_displayInt++
            status = 1
            displayNumber(lansia_displayInt)
        }

        increase_anak.setOnClickListener {
            anak_displayInt++
            status = 3
            displayNumber(anak_displayInt)
        }

        increase_dewasa.setOnClickListener {
            dewasa_displayInt++
            status = 2
            displayNumber(dewasa_displayInt)
        }

        textview_max_char_info.text = getString(R.string.max_char_280, 0)
        edittext_infotambahan.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) { }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textview_max_char_info.text = getString(R.string.max_char_280, p0?.length)
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayNumber(number: Int) {
        if (status == 3) {
            integer_number_anak.setText("" + number)
        } else if (status == 2) {
            integer_number_dewasa.setText("" + number)
        } else {
            integer_number_lansia.setText("" + number)
        }
    }

    private fun getIdKorban() {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("AkunKorban/$user")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    idKorban = p0.key.toString()
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("DatabaseReference : ", "user with id $user is not exist")
                    Toast.makeText(this@FormTemukanKorbanActivity, p0.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getDataKorban() {
        progressbar_name.visibility = View.VISIBLE

        val lansia = integer_number_lansia.text.toString().trim()
        val dewasa = integer_number_dewasa.text.toString().trim()
        val anak = integer_number_anak.text.toString().trim()
        val infoTambahan = edittext_infotambahan.text.toString().trim()

        val userId = ref.push().key.toString()

        val korban = InfoKorban(idKorban, lat, long, lansia.toInt(), dewasa.toInt(), anak.toInt(), infoTambahan, isMakanan, isMedis, isEvakuasi)
        ref.child(userId).setValue(korban).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressbar_name.visibility = View.GONE
                val intent = Intent(this, StatusTemukanKorbanActivity::class.java)
                intent.putExtra(EXTRA_ID_INFOKORBAN, userId)
                intent.putExtra(EXTRA_PREV_ACTIVITY, "Form")
                startActivity(intent)
                finish()


            } else {

            }
        }


    }
}