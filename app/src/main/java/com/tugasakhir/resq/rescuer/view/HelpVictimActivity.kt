package com.tugasakhir.resq.rescuer.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tugasakhir.resq.base.view.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.AkunKorban
import com.tugasakhir.resq.korban.model.InfoKorban
import com.tugasakhir.resq.rescuer.helper.VictimInfoData
import kotlinx.android.synthetic.main.activity_help_victim.*
import java.io.Serializable

class HelpVictimActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private var idRescuer: String = ""
    private lateinit var victimInfoData: VictimInfoData
    private lateinit var account : AkunKorban
    private var helpedVictimId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_victim)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.temukansaya_actionbar)
        actionBar.elevation = 0F

        victimInfoData = VictimInfoData()
        idRescuer = FirebaseAuth.getInstance().currentUser!!.uid

        textview_status.text = getString(R.string.help_accepted)
        button_open_chat_help.setOnClickListener {
            val intent = Intent(this, ChatMessageRescuerActivity::class.java)
            intent.putExtra("victim", account as Serializable)
            intent.putExtra("id", helpedVictimId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        getVictims()
    }

    private fun getVictims() {
        FirebaseDatabase.getInstance().getReference("KorbanTertolong")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val children = p0.children
                    children.forEach {
                        Log.d(
                            "HelpVictim",
                            "mau masuk euy ${it.child("rescuerArrived").value.toString()}}"
                        )
                        if (idRescuer == it.child("idRescuer").value.toString() &&
                            it.child("rescuerArrived").value.toString() == "false"
                        ) {
                            Log.d("HelpVictim", "masuk euy")
                            helpedVictimId = it.key.toString()
                            val victimInfoId = it.child("idInfoKorban").value.toString()
                            val isOnTheWay = it.child("onTheWay").value.toString().toBoolean()
                            if (isOnTheWay) {
                                button_rescuer_on_the_way.visibility = View.GONE
                                button_rescuer_arrived.visibility = View.VISIBLE
                                textview_status.text = getString(R.string.help_otw)
                            }
                            getInfoVictim(victimInfoId, helpedVictimId)
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("HelpedVictimError", p0.message)
                }
            })

    }

    private fun getInfoVictim(victimInfoId: String, helpedVictimId: String) {
        FirebaseDatabase.getInstance().getReference("InfoKorban")
            .child(victimInfoId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val victimInfo = p0.getValue(InfoKorban::class.java)
                    val uid = p0.child("idKorban").value.toString()
                    FirebaseDatabase.getInstance().reference.child("AkunKorban/$uid")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                account = p0.getValue(AkunKorban::class.java)!!
                                setLayout(victimInfo!!, account, helpedVictimId)
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                Log.d("HelpVictimError", p0.message)
                            }
                        })
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("HelpVictimError", p0.message)
                }
            })
    }

    private fun setLayout(victimInfo: InfoKorban?, account: AkunKorban?, helpedVictimId: String) {
        textview_name_victim_help.text = account?.name
        textview_phone_number_victim.text = account?.phone
        textview_call.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(("tel:${Uri.encode(account?.phone)}")))
            startActivity(intent)
        }

        if (account?.profilePhoto != "") {
            Picasso.get()
                .load(account?.profilePhoto)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_empty_pict)
                .error(R.drawable.ic_empty_pict)
                .into(imageview_victim_photo)
        }

        val helpType = when {
            victimInfo!!.bantuanMakanan -> {
                "Bantuan Makanan"
            }
            victimInfo.bantuanMedis -> {
                "Bantuan Medis"
            }
            else -> {
                "Bantuan Evakuasi"
            }
        }
        textview_help_type.text = helpType
        textview_address_victim_help.text = victimInfoData.getAddress(
            victimInfo.latitude.toDouble(),
            victimInfo.longitude.toDouble(),
            this
        )

        textview_number_of_elderly_victim.text =
            Html.fromHtml(getString(R.string.number_of_elderly, victimInfo.jumlahLansia.toString()))
        textview_number_of_adult_victim.text =
            Html.fromHtml(getString(R.string.number_of_adults, victimInfo.jumlahDewasa.toString()))
        textview_number_of_child_victim.text =
            Html.fromHtml(getString(R.string.number_of_children, victimInfo.jumlahAnak.toString()))

        textview_victim_additional_information.text = victimInfo.infoTambahan

        //button
        button_rescuer_on_the_way.setOnClickListener {
            button_rescuer_arrived.visibility = View.VISIBLE
            button_rescuer_on_the_way.visibility = View.GONE
            FirebaseDatabase.getInstance().reference.child("KorbanTertolong/$helpedVictimId")
                .child("accepted").setValue(false)
            FirebaseDatabase.getInstance().reference.child("KorbanTertolong/$helpedVictimId")
                .child("onTheWay").setValue(true)
        }

        button_rescuer_arrived.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("KorbanTertolong/$helpedVictimId")
                .child("onTheWay").setValue(false)
            FirebaseDatabase.getInstance().reference.child("KorbanTertolong/$helpedVictimId")
                .child("rescuerArrived").setValue(true)

            //delete chats
            FirebaseDatabase.getInstance().reference.child("Messages/$helpedVictimId/$idRescuer/${account?.id}")
                .removeValue()
            FirebaseDatabase.getInstance().reference.child("Messages/$helpedVictimId/${account?.id}/$idRescuer")
                .removeValue()

            //isHelping false
            FirebaseDatabase.getInstance().reference.child("Rescuers/$idRescuer")
                .child("helping").setValue(false)
            //intent ke halaman maps
            val intent = Intent(this, ThankYouRescuerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        button_open_maps_help.setOnClickListener {
            val location = "${victimInfo.latitude} ${victimInfo.longitude}"
            val bundle = Bundle()
            bundle.putString("location", location)
            val intent = Intent(this, OpenMapsActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
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

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}