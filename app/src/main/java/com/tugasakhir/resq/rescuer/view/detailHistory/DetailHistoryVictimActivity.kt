package com.tugasakhir.resq.rescuer.view.detailHistory

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.KorbanTertolong
import com.tugasakhir.resq.rescuer.helper.VictimInfoData
import kotlinx.android.synthetic.main.activity_detail_history_victim.*

class DetailHistoryVictimActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private var victimInfoData = VictimInfoData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_history_victim)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.history)
        actionBar.elevation = 0F

        val helpedVictim = intent?.getSerializableExtra("helpedVictim") as KorbanTertolong

        setViews(helpedVictim)
    }

    private fun setViews(helpedVictim: KorbanTertolong) {
        textview_detail_victim_history_date.text = helpedVictim.date
        FirebaseDatabase.getInstance().getReference("Rescuers/${helpedVictim.idRescuer}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    textview_detail_victim_history_name.text = p0.child("name").value.toString()
                    textview_detail_victim_history_phone.text = p0.child("phone").value.toString()
                    val profilePhoto = p0.child("profilePhoto").value.toString()
                    if (profilePhoto != "") {
                        Picasso.get()
                            .load(profilePhoto)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.ic_empty_pict)
                            .placeholder(R.drawable.ic_empty_pict)
                            .into(imageview_rescuer_photo)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("VictimHistoryDetail", p0.message)
                }
            })

        FirebaseDatabase.getInstance().getReference("InfoKorban/${helpedVictim.idInfoKorban}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val address = victimInfoData.getAddress(
                        p0.child("latitude").value.toString().toDouble(),
                        p0.child("longitude").value.toString().toDouble(),
                        this@DetailHistoryVictimActivity
                    )

                    textview_detail_victim_history_address.text = address

                    val helpType = when {
                        p0.child("bantuanEvakuasi").value.toString().toBoolean() -> {
                            "Bantuan Evakuasi"
                        }
                        p0.child("bantuanMakanan").value.toString().toBoolean() -> {
                            "Bantuan Makanan"
                        }
                        else -> {
                            "Bantuan Medis"
                        }
                    }

                    textview_detail_victim_history_help_type.text = helpType

                    textview_history_number_of_elderly_victim.text =
                        Html.fromHtml(
                            getString(
                                R.string.number_of_elderly,
                                p0.child("jumlahLansia").value.toString()
                            )
                        )
                    textview_history_number_of_adult_victim.text =
                        Html.fromHtml(
                            getString(
                                R.string.number_of_adults,
                                p0.child("jumlahDewasa").value.toString()
                            )
                        )
                    textview_history_number_of_child_victim.text =
                        Html.fromHtml(
                            getString(
                                R.string.number_of_children,
                                p0.child("jumlahAnak").value.toString()
                            )
                        )

                    textview_history_victim_additional_information.text =
                        p0.child("infoTambahan").value.toString()

                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("VictimHistoryDetail", p0.message)
                }
            })
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