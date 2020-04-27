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
import kotlinx.android.synthetic.main.fragment_temukansayastatus1_korban.progressbar_name

class StatusSentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_temukansayastatus1_korban, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button_batalkan.setOnClickListener {

            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle(R.string.alert_batalkan)
            builder.setMessage(R.string.alert_batalkan_status1)
            builder.setNegativeButton(R.string.alert_tetap_batalkan){_,_ ->
                progressbar_name.visibility = View.VISIBLE
                removeData()

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
        fun newInstance(): StatusSentFragment = StatusSentFragment()
    }

    private fun removeData() {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().getReference("InfoKorban")
            .addListenerForSingleValueEvent(object  : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val children = p0.children
                    children.forEach {

                        if (user == it.child("idKorban").value.toString()) {
                            confirmRemoveData(it.key.toString())
                        }

                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)                }
            })
    }

    private fun confirmRemoveData(userId: String) {
        var index = 1
        FirebaseDatabase.getInstance().getReference("KorbanTertolong")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val children = p0.children
                    children.forEach {

                        if (userId == it.child("idInfoKorban").value.toString()) {
                            return
                        } else if (p0.childrenCount == index.toLong()) {
                            FirebaseDatabase.getInstance().reference.child("InfoKorban/$userId").removeValue()
                        }
                        index++
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })

    }
}