package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.fragment_temukansayastatus1_korban.*

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
            builder.setTitle("Batalkan Permintaan Bantuan")
            builder.setMessage("Bantuan Anda akan segera ditangani, apakah Anda yakin ingin membatalkan?")
            builder.setNegativeButton("Tetap Batalkan"){_,_ ->
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity!!.finish()
            }

            builder.setPositiveButton("Jangan Batalkan"){_,_ ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }
    }

    companion object {
        fun newInstance(): StatusSentFragment = StatusSentFragment()
    }
}