package com.tugasakhir.resq.rescuer.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    companion object {
        fun newInstance(): HomeFragment =
            HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_waterlevel_beranda.setOnClickListener {
            button_waterlevel_beranda.setBackgroundResource(R.drawable.shape_bordered_button_clicked)
            button_waterlevel_beranda.setTextColor(resources.getColor(R.color.white_dark))
            val intent = Intent(activity, WaterLevelActivity::class.java)
            startActivity(intent)
        }
    }
}
