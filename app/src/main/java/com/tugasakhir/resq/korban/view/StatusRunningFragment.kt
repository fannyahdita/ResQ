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
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.fragment_temukansayastatus1_korban.*
import kotlinx.android.synthetic.main.fragment_temukansayastatus1_korban.button_batalkan
import kotlinx.android.synthetic.main.fragment_temukansayastatus3_korban.*

class StatusRunningFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_temukansayastatus3_korban, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val rescuerName = arguments!!.getString("rescuerName")
        val rescuerPhone = arguments!!.getString("rescuerPhone")

        textview_nama_rescuer.text = rescuerName
        textview_nomor_rescuer.text = rescuerPhone

    }
    companion object {
        fun newInstance(rescuerName: String, rescuerPhone: String): StatusRunningFragment {
            val args = Bundle()
            args.putString("rescuerName", rescuerName)
            args.putString("rescuerPhone", rescuerPhone)
            val fragment = StatusRunningFragment()
            fragment.arguments = args
            return fragment
        }
    }
}