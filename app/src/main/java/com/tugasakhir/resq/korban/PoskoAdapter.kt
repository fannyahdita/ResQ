package com.tugasakhir.resq.korban

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.model.Posko
import kotlinx.android.synthetic.main.list_posko.view.*

class PoskoAdapter(val posko: List<Posko>) : RecyclerView.Adapter<PoskoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_list_posko, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = posko.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPosko = posko[position]
        holder.textview_lokasi_posko.text = currentPosko.poskoName
        holder.textview_alamat_posko.text = currentPosko.address
        holder.textview_kapasitas.text = currentPosko.toString()
        holder.textview_jarak.text = currentPosko.city
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view){
        val textview_lokasi_posko: TextView = view.textview_lokasi_posko
        val textview_alamat_posko: TextView = view.textview_alamat_posko
        val textview_kapasitas: TextView = view.textview_kapasitas
        val textview_jarak: TextView = view.textview_jarak

//        fun setUpView(posko: Posko?) {
//            textview_lokasi_posko.text = posko?.poskoName
//            textview_alamat_posko.text = posko?.address
//            textview_kapasitas.text = posko?.capacity.toString()
//            textview_jarak.text = posko?.city
//
//        }

    }
}