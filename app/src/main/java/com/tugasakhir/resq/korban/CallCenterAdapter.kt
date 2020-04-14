package com.tugasakhir.resq.korban

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.CallCenter
import kotlinx.android.synthetic.main.card_call_center_1.view.*

class CallCenterAdapter : BaseAdapter {

    private var callCenter: List<CallCenter> = ArrayList()
    var context: Context? = null

    constructor(context: Context?, foodsList: ArrayList<CallCenter>) : super() {
        this.context = context
        this.callCenter = foodsList
    }
    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val callCenter = this.callCenter[position]

        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var callCenterView = inflator.inflate(R.layout.card_call_center_1, null)
        callCenterView.textview_call_center.text = callCenter.name
        callCenterView.textview_number_call_center.text = callCenter.number

        return callCenterView

    }

    override fun getItem(position: Int): Any {
        return callCenter[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return callCenter.size
    }
}