package com.tugasakhir.resq.korban.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.CallCenterAdapter
import com.tugasakhir.resq.korban.CallCenterSarAdapter
import com.tugasakhir.resq.korban.model.CallCenter
import kotlinx.android.synthetic.main.fragment_call_center.*

class CallCenterFragment : Fragment() {
    var callCenterList = ArrayList<CallCenter?>()
    var sarCallCenterList = ArrayList<CallCenter?>()
    private val callCenterAdapter = CallCenterAdapter()
    private val sarCallCenterAdapter = CallCenterSarAdapter()

    companion object {
        fun newInstance(): CallCenterFragment =
            CallCenterFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_call_center, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        list_call_center.layoutManager = GridLayoutManager(activity, 3, LinearLayoutManager.VERTICAL, false)
        list_call_center.adapter = callCenterAdapter

        list_call_center_sar.layoutManager = GridLayoutManager(activity, 1, LinearLayoutManager.VERTICAL, false)
        list_call_center_sar.adapter = sarCallCenterAdapter

        callCenterList.add(CallCenter("Ambulans 1", "118"))
        callCenterList.add(CallCenter("Ambulans 2", "119"))
        callCenterList.add(CallCenter("BPBD DKI", "112"))
        callCenterList.add(CallCenter("Basarnas", "115"))
        callCenterList.add(CallCenter("PLN", "123"))
        callCenterList.add(CallCenter("Polisi", "110"))

        callCenterAdapter.setCallCenter(callCenterList)

        sarCallCenterList.add(CallCenter("Call Center 1", "021-5501512"))
        sarCallCenterList.add(CallCenter("Call Center 2", "021-550511111"))
        sarCallCenterList.add(CallCenter("Jakarta Selatan", "021-7515054"))
        sarCallCenterList.add(CallCenter("Jakarta Timur", "021-85904904"))
        sarCallCenterList.add(CallCenter("Jakarta Pusat", "021-6344215"))
        sarCallCenterList.add(CallCenter("Jakarta Barat", "021-5682284"))
        sarCallCenterList.add(CallCenter("Jakarta Utara", "021-43931063"))
        sarCallCenterList.add(CallCenter("Jakarta Selatan", "021-7515054"))
        sarCallCenterList.add(CallCenter("Jakarta Timur", "021-85904904"))
        sarCallCenterList.add(CallCenter("Jakarta Pusat", "021-6344215"))
        sarCallCenterList.add(CallCenter("Jakarta Barat", "021-5682284"))
        sarCallCenterList.add(CallCenter("Jakarta Utara", "021-43931063"))

        sarCallCenterAdapter.setCallCenter(sarCallCenterList)
    }

}