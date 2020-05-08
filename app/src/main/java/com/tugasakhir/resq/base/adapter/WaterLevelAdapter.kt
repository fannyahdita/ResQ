package com.tugasakhir.resq.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tugasakhir.resq.R
import com.tugasakhir.resq.base.model.WaterGate
import kotlinx.android.synthetic.main.item_water_level.view.*

class WaterLevelAdapter  : RecyclerView.Adapter<WaterLevelAdapter.ViewHolder>() {

    private var waterGates : List<WaterGate> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_water_level, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentWaterGate = waterGates[position]

        holder.itemView.water_level_name.text = currentWaterGate.name
    }

    override fun getItemCount(): Int {
        return waterGates.size
    }

    fun setWaterGates(waterGates: List<WaterGate>) {
        this.waterGates = waterGates
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

