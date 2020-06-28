package com.tugasakhir.resq.korban.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.KorbanTertolong
import com.tugasakhir.resq.rescuer.helper.VictimInfoData
import com.tugasakhir.resq.korban.view.DetailHistoryKorbanActivity
import kotlinx.android.synthetic.main.item_history_victim.view.*
import java.io.Serializable

class HistoryKorbanAdapter : RecyclerView.Adapter<HistoryKorbanAdapter.ViewHolder>() {

    private var history: List<KorbanTertolong> = ArrayList()
    private var victimInfoData =
        VictimInfoData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_history_victim, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentHistory = history[position]
        holder.itemView.textview_victim_history_item_datetime.text = currentHistory.date

        FirebaseDatabase.getInstance().getReference("Rescuers/${currentHistory.idRescuer}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    holder.itemView.textview_victim_history_item_rescuer_name.text = p0.child("name").value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("VictimAdapterError", p0.message)
                }
            })

        FirebaseDatabase.getInstance().getReference("InfoKorban/${currentHistory.idInfoKorban}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    val address = victimInfoData.getAddress(
                        p0.child("latitude").value.toString().toDouble(),
                        p0.child("longitude").value.toString().toDouble(), holder.itemView.context
                    )

                    holder.itemView.textview_victim_history_item_address.text = address

                    val helpType = when {
                        p0.child("bantuanEvakuasi").value.toString().toBoolean() -> {
                            "Bantuan Evakuasi"
                        }
                        p0.child("bantuanMakanan").value.toString().toBoolean() -> {
                            "Bantuan Makanan"
                        }
                        else -> {
                            "Bantuan Medis"
                        }
                    }

                    holder.itemView.textview_victim_history_item_help_type.text = helpType
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("VictimAdapterError", p0.message)
                }
            })

        holder.itemView.item_history_victim.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailHistoryKorbanActivity::class.java)
            intent.putExtra("helpedVictim", currentHistory as Serializable)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return history.size
    }

    fun setHistory(history: List<KorbanTertolong>) {
        this.history = history
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}