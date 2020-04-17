package com.tugasakhir.resq.korban.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.CallCenterAdapter
import com.tugasakhir.resq.korban.model.CallCenter
import kotlinx.android.synthetic.main.fragment_call_center.*

class CallCenterFragment : Fragment() {
    var callCenterList = ArrayList<CallCenter>()
    var sarCallCenterList = ArrayList<CallCenter>()
    var adapter: CallCenterAdapter? = null

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


        callCenterList.add(CallCenter("Ambulans 1", "118"))
        callCenterList.add(CallCenter("Ambulans 2", "119"))
        callCenterList.add(CallCenter("BPBD DKI", "112"))
        callCenterList.add(CallCenter("Basarnas", "115"))
        callCenterList.add(CallCenter("PLN", "123"))
        callCenterList.add(CallCenter("Polisi", "110"))

        adapter = CallCenterAdapter(context, callCenterList, 1)
        grid_view_call_center.adapter = adapter

        sarCallCenterList.add(CallCenter("Call Center 1", "021-5501512"))
        sarCallCenterList.add(CallCenter("Call Center 2", "021-550511111"))
        sarCallCenterList.add(CallCenter("Jakarta Selatan", "021-7515054"))
        sarCallCenterList.add(CallCenter("Jakarta Timur", "021-85904904"))
        sarCallCenterList.add(CallCenter("Jakarta Pusat", "021-6344215"))
        sarCallCenterList.add(CallCenter("Jakarta Barat", "021-5682284"))
        sarCallCenterList.add(CallCenter("Jakarta Utara", "021-43931063"))

        adapter = CallCenterAdapter(context, sarCallCenterList, 2)
        grid_view_call_center_sar.adapter = adapter
    }

}