package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.fragment_temukansayastatus1_korban.*
import kotlinx.android.synthetic.main.fragment_temukansayastatus1_korban.button_batalkan
import kotlinx.android.synthetic.main.fragment_temukansayastatus1_korban.progressbar_name
import kotlinx.android.synthetic.main.fragment_temukansayastatus2_korban.*

class StatusAcceptedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_temukansayastatus2_korban, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val idInfoKorban = arguments!!.getString("idInfoKorban")
        val idKorbanTertolong = arguments!!.getString("idKorbanTertolong")
        val rescuerName = arguments!!.getString("rescuerName")
        val rescuerPhone = arguments!!.getString("rescuerPhone")

        textview_nama_rescuer.text = rescuerName
        textview_nomor_rescuer.text = rescuerPhone


        button_batalkan.setOnClickListener {
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle(R.string.alert_batalkan)
            builder.setMessage(R.string.alert_batalkan_status2)
            builder.setNegativeButton(R.string.alert_tetap_batalkan){_,_ ->
                progressbar_name.visibility = View.VISIBLE

                removeData(idInfoKorban, idKorbanTertolong)

                val user = FirebaseAuth.getInstance().currentUser?.uid
                FirebaseDatabase.getInstance().reference.child("AkunKorban/$user").child("askingHelp").setValue(false)

                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity!!.finish()
            }

            builder.setPositiveButton(R.string.alert_jangan_batalkan){_,_ ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
    companion object {
        fun newInstance(idInfoKorban: String, idKorbanTertolong: String, rescuerName: String, rescuerPhone: String): StatusAcceptedFragment {
            val args = Bundle()
            args.putString("idInfoKorban", idInfoKorban)
            args.putString("idKorbanTertolong", idKorbanTertolong)
            args.putString("rescuerName", rescuerName)
            args.putString("rescuerPhone", rescuerPhone)
            val fragment = StatusAcceptedFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun getRescuerData(idRescuer: String?) {
        FirebaseDatabase.getInstance().reference.child("Rescuers/$idRescuer")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val name = p0.child("name").value
                    val phone = p0.child("phone").value
                    textview_nama_rescuer.text = name.toString()
                    textview_nomor_rescuer.text = phone.toString()
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })
    }

    private fun removeData(idInfoKorban: String?, idKorbanTertolong: String?) {
        FirebaseDatabase.getInstance().reference.child("InfoKorban/$idInfoKorban").removeValue()
        FirebaseDatabase.getInstance().reference.child("KorbanTertolong/$idKorbanTertolong").removeValue()
        progressbar_name.visibility = View.GONE

    }
}