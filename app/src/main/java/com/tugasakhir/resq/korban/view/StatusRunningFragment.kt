package com.tugasakhir.resq.korban.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tugasakhir.resq.R

class StatusRunningFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_temukansayastatus3_korban, container, false)
    }

    companion object {
        fun newInstance(): StatusRunningFragment = StatusRunningFragment()
    }
}