package com.tugasakhir.resq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class Fragment_TemukanSaya_Korban : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_temukansaya_korban, container, false)

    companion object {
        fun newInstance(): Fragment_TemukanSaya_Korban = Fragment_TemukanSaya_Korban()
    }

}