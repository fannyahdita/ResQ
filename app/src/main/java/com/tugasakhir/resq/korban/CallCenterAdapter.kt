package com.tugasakhir.resq.korban

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.CallCenter
import kotlinx.android.synthetic.main.card_call_center_1.view.*
import kotlinx.android.synthetic.main.card_call_center_2.view.*

class CallCenterAdapter : BaseAdapter {

    private var callCenter: List<CallCenter> = ArrayList()
    var context: Context? = null
    var state: Int = 0

    constructor(context: Context?, foodsList: ArrayList<CallCenter>, state: Int) : super() {
        this.context = context
        this.callCenter = foodsList
        this.state = state

    }
    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val callCenter = this.callCenter[position]

        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var callCenterView: View
        if (state == 1) {
            callCenterView = inflator.inflate(R.layout.card_call_center_1, null)
            callCenterView.textview_call_center.text = callCenter.name
            callCenterView.textview_number_call_center.text = callCenter.number
            callCenterView.card_1.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(callCenter.number.trim())))
                (context)?.startActivity(intent)
            }
        } else {
            callCenterView = inflator.inflate(R.layout.card_call_center_2, null)
            callCenterView.textview_call_center_sar.text = callCenter.name
            callCenterView.textview_number_call_center_sar.text = callCenter.number

            callCenterView.card_2.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(callCenter.number.trim())))
                (context)?.startActivity(intent)
            }
        }

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