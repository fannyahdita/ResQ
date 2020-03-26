package com.tugasakhir.resq.rescuer

import android.content.Context
import android.location.Geocoder
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.Korban
import java.util.*

class InfoKorbanData {

    fun setDetailMaps(victimId: String, context: Context) {
        FirebaseDatabase.getInstance().getReference("InfoKorban")
            .child(victimId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val latitude = p0.child("latitude").value.toString()
                    val longitude = p0.child("longitude").value.toString()
                    val elderly = p0.child("jumlahLansia").value.toString().toInt()
                    val adult = p0.child("jumlahDewasa").value.toString().toInt()
                    val children = p0.child("jumlahAnak").value.toString().toInt()
                    val info = p0.child("infoTambahan").value.toString()
                    val isFoodNeeded = p0.child("bantuanMakanan").value.toString().toBoolean()
                    val isMedicNeeded = p0.child("bantuanMedis").value.toString().toBoolean()
                    val isEvacuationNeeded =
                        p0.child("bantuanEvakuasi").value.toString().toBoolean()
                    val uid = p0.child("idKorban").value.toString()

                    FirebaseDatabase.getInstance().getReference("AkunKorban")
                        .child(uid).child("name")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                val korban = Korban(
                                    uid,
                                    latitude,
                                    longitude,
                                    false,
                                    elderly,
                                    adult,
                                    children,
                                    info,
                                    isFoodNeeded,
                                    isMedicNeeded,
                                    isEvacuationNeeded
                                )
                                makeAlertDialog(korban, p0.value.toString(), context)
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                Log.d("TemukanSayaError : ", p0.message)
                            }
                        })
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })
    }

    private fun makeAlertDialog(korban: Korban, name: String, context: Context) {
        val alertDialog = AlertDialog.Builder(context)
        val bantuan = if (korban.bantuanMakanan) {
            "Bantuan Makanan"
        } else if (korban.bantuanMedis) {
            "Bantuan Medis"
        } else {
            "Bantuan Evakuasi"
        }
        val address = getAddress(korban.latitude.toDouble(), korban.longitude.toDouble(), context)
        alertDialog.setTitle(name)
        alertDialog.setMessage(
            Html.fromHtml(
                context.getString(
                    R.string.detail_victim,
                    bantuan,
                    address,
                    korban.jumlahLansia.toString(),
                    korban.jumlahDewasa.toString(),
                    korban.jumlahAnak.toString(),
                    korban.infoTambahan
                )
            )
        )
        alertDialog.create().show()
    }


    fun getAddress(latitude: Double, longitude: Double, context: Context): String {
        val geocode = Geocoder(context, Locale.getDefault())
        val address = geocode.getFromLocation(latitude, longitude, 1)
        return address[0].getAddressLine(0)
    }
}