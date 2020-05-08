package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.model.Rescuer
import kotlinx.android.synthetic.main.fragment_temukansayastatus3_korban.*
import java.io.Serializable

class StatusRunningFragment : Fragment() {

    private lateinit var rescuer: Rescuer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_temukansayastatus3_korban, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val idKorbanTertolong = arguments?.getString("idKorbanTertolong")!!
        rescuer = arguments!!.getSerializable("rescuer") as Rescuer

        textview_nama_rescuer.text = rescuer.name
        textview_nomor_rescuer.text = rescuer.phone
        textview_instansi_rescuer.text = rescuer.instansi

        if (rescuer?.profilePhoto == "") {
            image_status3.setImageResource(R.drawable.ic_empty_pict)
        } else {
            Picasso.get()
                .load(rescuer?.profilePhoto)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_empty_pict)
                .error(R.drawable.ic_empty_pict)
                .into(image_status3)
        }

        button_kirimpesan.setOnClickListener {
            val intent = Intent(activity, ChatMessageVictimActivity::class.java)
            intent.putExtra("rescuer", rescuer as Serializable)
            intent.putExtra("id", idKorbanTertolong)
            startActivity(intent)
        }

    }
    companion object {
        fun newInstance(rescuer: Rescuer?, idKorbanTertolong: String): StatusRunningFragment {
            val args = Bundle()
            args.putSerializable("rescuer", rescuer)
            args.putString("idKorbanTertolong", idKorbanTertolong)
            val fragment = StatusRunningFragment()
            fragment.arguments = args
            return fragment
        }
    }
}