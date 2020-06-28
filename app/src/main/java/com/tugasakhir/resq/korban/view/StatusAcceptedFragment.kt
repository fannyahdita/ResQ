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
import com.squareup.picasso.Picasso
import com.tugasakhir.resq.R
import com.tugasakhir.resq.base.view.MainActivity
import com.tugasakhir.resq.rescuer.model.Rescuer
import kotlinx.android.synthetic.main.fragment_status2_korban.*
import java.io.Serializable

class StatusAcceptedFragment : Fragment() {

    private lateinit var rescuer: Rescuer
    private var idKorbanTertolong = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_status2_korban, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val idInfoKorban = arguments?.getString("idInfoKorban")
        idKorbanTertolong = arguments?.getString("idKorbanTertolong")!!
        rescuer = arguments!!.getSerializable("rescuer") as Rescuer

        textview_nama_rescuer.text = rescuer.name
        textview_nomor_rescuer.text = rescuer.phone
        textview_instansi_rescuer.text = rescuer.instansi

        if (rescuer?.profilePhoto == "") {
            image_status2.setImageResource(R.drawable.ic_empty_pict)
        } else {
            Picasso.get()
                .load(rescuer?.profilePhoto)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_empty_pict)
                .error(R.drawable.ic_empty_pict)
                .into(image_status2)
        }

        button_batalkan.setOnClickListener {
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle(R.string.alert_batalkan)
            builder.setMessage(R.string.alert_batalkan_status2)
            builder.setNegativeButton(R.string.alert_tetap_batalkan) { _, _ ->
                progressbar_name.visibility = View.VISIBLE
                removeData(idInfoKorban, idKorbanTertolong)
                val user = FirebaseAuth.getInstance().currentUser?.uid
                FirebaseDatabase.getInstance().reference.child("AkunKorban/$user")
                    .child("askingHelp").setValue(false)

                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity!!.finish()
            }

            builder.setPositiveButton(R.string.alert_jangan_batalkan) { _, _ ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        button_kirimpesan.setOnClickListener {
            val intent = Intent(activity, ChatMessageVictimActivity::class.java)
            intent.putExtra("rescuer", rescuer as Serializable)
            intent.putExtra("id", idKorbanTertolong)
            startActivity(intent)
        }
    }

    companion object {
        fun newInstance(
            idInfoKorban: String,
            idKorbanTertolong: String,
            rescuer: Rescuer?
        ): StatusAcceptedFragment {
            val args = Bundle()
            args.putString("idInfoKorban", idInfoKorban)
            args.putString("idKorbanTertolong", idKorbanTertolong)
            args.putSerializable("rescuer", rescuer)
            val fragment = StatusAcceptedFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun removeData(idInfoKorban: String?, idKorbanTertolong: String?) {
        notifyRescuerWhenCancelled(idKorbanTertolong) {
            FirebaseDatabase.getInstance().reference.child("Rescuers/$it").child("helping")
                .setValue(false)
            FirebaseDatabase.getInstance().reference.child("KorbanTertolong/$idKorbanTertolong")
                .removeValue()
            FirebaseDatabase.getInstance().reference.child("InfoKorban/$idInfoKorban").removeValue()
        }
        progressbar_name.visibility = View.GONE
    }

    private fun notifyRescuerWhenCancelled(
        idKorbanTertolong: String?,
        resulHandler: (String) -> Unit
    ) {
        FirebaseDatabase.getInstance().reference.child("KorbanTertolong/$idKorbanTertolong")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val id = p0.child("idRescuer").value.toString()
                    if (id != "null") {
                        resulHandler(id)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("RemoveRescuer", p0.message)
                }
            })
    }
}