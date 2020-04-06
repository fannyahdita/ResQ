package com.tugasakhir.resq.rescuer.adapter

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
import com.tugasakhir.resq.rescuer.VictimInfoData
import kotlinx.android.synthetic.main.item_history_rescuer.view.*

class HistoryRescuerAdapter : RecyclerView.Adapter<HistoryRescuerAdapter.ViewHolder>() {

    private var helpedVictim: List<KorbanTertolong> = ArrayList()
    private var victimInfoData = VictimInfoData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_history_rescuer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentHelpedVictim = helpedVictim[position]
        val infoVictimId = currentHelpedVictim.idInfoKorban

        holder.itemView.textview_history_item_rescuer_datetime.text = currentHelpedVictim.date

        FirebaseDatabase.getInstance().getReference("InfoKorban").child(infoVictimId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val address = victimInfoData.getAddress(
                        p0.child("latitude").value.toString().toDouble(),
                        p0.child("longitude").value.toString().toDouble(), holder.itemView.context
                    )

                    holder.itemView.textview_history_item_rescuer_address.text = address

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

                    holder.itemView.textview_history_item_rescuer_help_type.text = helpType

                    val idVictimAccount = p0.child("idKorban").value.toString()

                    FirebaseDatabase.getInstance().getReference("AkunKorban/$idVictimAccount")
                        .child("name")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                holder.itemView.textview_history_item_rescuer_name.text =
                                    p0.value.toString()
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                Log.d("History Rescuer Adapter", p0.message)
                            }
                        })

                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("History Rescuer Adapter", p0.message)
                }
            })
    }

    override fun getItemCount(): Int {
        return helpedVictim.size
    }

    fun setHelpedVictim(helpedVictim: List<KorbanTertolong>) {
        this.helpedVictim = helpedVictim
        notifyDataSetChanged()
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}