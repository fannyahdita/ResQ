package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.PoskoAdapter
import com.tugasakhir.resq.rescuer.model.Posko
import com.tugasakhir.resq.rescuer.view.AddPoskoLocationActivity
import kotlinx.android.synthetic.main.fragment_list_posko.*

class PoskoListFragment : Fragment() {

    private val poskoAdapter = PoskoAdapter()
    private lateinit var currentPosko: Posko
    private var role = ""

    companion object {
        const val ROLE = ""
        fun newInstance(isKorban: Boolean, lat: String, long: String): PoskoListFragment {
            val fragment = PoskoListFragment()
            val bundle = Bundle()
            bundle.putString("lat", lat)
            bundle.putString("long", long)
            if (isKorban) {
                bundle.putString(ROLE, "victim")
            } else {
                bundle.putString(ROLE, "rescuer")
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    private var TAG = "LIST POSKO "
    val listPosko: ArrayList<Posko?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        role = arguments?.getString(ROLE).toString()
        return inflater.inflate(R.layout.fragment_list_posko, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "MASUK INIT SEBAGAi $role")
        posko_recycler_view.layoutManager = LinearLayoutManager(activity)
        posko_recycler_view.adapter = poskoAdapter

        val lat = arguments!!.getString("lat")
        val long = arguments!!.getString("long")

        fetchPoskoData(lat, long)
    }

    override fun onResume() {
        super.onResume()
        if (role == "rescuer") {
            textview_add_posko_rescuer.visibility = View.VISIBLE
        }
        textview_add_posko_rescuer.setOnClickListener {
            val intent = Intent(activity, AddPoskoLocationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchPoskoData(lat: String?, long: String?) {
        FirebaseDatabase.getInstance().getReference("Posko")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val children = p0.children
                    children.forEach { posko ->
                        val open = posko.child("open").value.toString().toBoolean()
                        if(open) {
                            currentPosko = posko.getValue(Posko::class.java)!!
                            listPosko.add(currentPosko)
                        }

                    }
                    poskoAdapter.setPosko(listPosko, lat, long)
                }


                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })

    }


}