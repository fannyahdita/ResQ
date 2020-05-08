package com.tugasakhir.resq.base.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tugasakhir.resq.R
import com.tugasakhir.resq.base.model.CallCenter
import kotlinx.android.synthetic.main.card_call_center_2.view.*

class CallCenterSarAdapter : RecyclerView.Adapter<CallCenterSarAdapter.ViewHolder>() {

    private var callCenter: List<CallCenter?> = ArrayList()

    fun setCallCenter(callCenter: ArrayList<CallCenter?>) {
        this.callCenter = callCenter
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_call_center_2, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun getItemCount() = callCenter.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val callCenter = callCenter[position]
        val context = holder.itemView.context

        holder.itemView.textview_call_center_sar.text = callCenter?.name
        holder.itemView.textview_number_call_center_sar.text = callCenter?.number

        holder.itemView.call_center_card_sar.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(callCenter!!.number.trim())))
            (context)?.startActivity(intent)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view)
}