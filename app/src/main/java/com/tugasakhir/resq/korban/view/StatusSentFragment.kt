package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tugasakhir.resq.base.view.MainActivity
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

        val idInfoKorban = arguments?.getString("idInfoKorban")

        button_batalkan.setOnClickListener {

            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle(R.string.alert_batalkan)
            builder.setMessage(R.string.alert_batalkan_status1)
            builder.setNegativeButton(R.string.alert_tetap_batalkan){_,_ ->
                progressbar_name.visibility = View.VISIBLE
                removeData(idInfoKorban)

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
        fun newInstance(idInfoKorban: String): StatusSentFragment {
            val args = Bundle()
            args.putString("idInfoKorban", idInfoKorban)
            val fragment = StatusSentFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun removeData(idInfoKorban: String?) {
        FirebaseDatabase.getInstance().reference.child("InfoKorban/$idInfoKorban").removeValue()
        progressbar_name.visibility = View.GONE
    }
}