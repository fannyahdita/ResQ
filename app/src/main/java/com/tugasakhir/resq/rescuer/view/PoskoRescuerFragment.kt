package com.tugasakhir.resq.rescuer.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.fragment_posko_rescuer.*


class PoskoRescuerFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posko_rescuer, container, false)
    }

    companion object {
        fun newInstance(): PoskoRescuerFragment =
            PoskoRescuerFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_add_posko.setOnClickListener {
            val intent = Intent(activity, AddPoskoRescuerActivity::class.java)
            startActivity(intent)
        }
    }
}
