package com.tugasakhir.resq.korban.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.PoskoAdapter
import com.tugasakhir.resq.rescuer.model.Posko

class PoskoListFragment: Fragment() {

    private val poskoAdapter = PoskoAdapter()

    lateinit var rootView: View
    lateinit var recyclerView: RecyclerView
    val listPosko: ArrayList<Posko?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_list_posko, container, false)
        initView()
        return rootView

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateComponent()
    }

    companion object {
        var TAG = PoskoListFragment::class.java.simpleName
        const val ARG_POSITION: String = "positioin"

        fun newInstance(): PoskoListFragment {
            var fragment = PoskoListFragment();
            val args = Bundle()
            args.putInt(ARG_POSITION, 1)
            fragment.arguments = args
            return fragment
        }
    }

    private fun onCreateComponent() {
    }

    private fun initView() {
        fetchPoskoData()
    }

    private fun initializeRecyclerView() {
        recyclerView = rootView.findViewById(R.id.posko_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = poskoAdapter
        poskoAdapter.setPosko(listPosko)
    }

    private fun fetchPoskoData() {
        FirebaseDatabase.getInstance().getReference("Posko")
            .addListenerForSingleValueEvent(object  : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val children = p0.children
                    children.forEach {
                        val posko = p0.getValue(Posko::class.java)
                        listPosko.add(posko)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TemukanSayaError : ", p0.message)
                }
            })

        initializeRecyclerView()
    }


}