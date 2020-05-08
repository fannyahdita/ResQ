package com.tugasakhir.resq.base.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.AkunKorban
import com.tugasakhir.resq.korban.model.KorbanTertolong
import com.tugasakhir.resq.korban.view.EditProfileKorbanActivity
import com.tugasakhir.resq.rescuer.adapter.HistoryRescuerAdapter
import com.tugasakhir.resq.korban.adapter.HistoryVictimAdapter
import com.tugasakhir.resq.rescuer.model.Rescuer
import com.tugasakhir.resq.rescuer.view.EditProfileRescuerActivity
import kotlinx.android.synthetic.main.fragment_profile_rescuer.*

class ProfileFragment : Fragment() {

    private var role = ""

    companion object {
        const val ROLE = ""

        fun newInstance(isKorban: Boolean): ProfileFragment {
            val fragment = ProfileFragment()
            val bundle = Bundle()
            if (isKorban) {
                bundle.putString(ROLE, "victim")
            } else {
                bundle.putString(ROLE, "rescuer")
            }

            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var rescuerHistory: ArrayList<KorbanTertolong>
    private lateinit var victimHistory: ArrayList<KorbanTertolong>
    private var historyRescuerAdapter = HistoryRescuerAdapter()
    private var historyVictimAdapter =
        HistoryVictimAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!
        role = arguments?.getString(ROLE).toString()

        return inflater.inflate(R.layout.fragment_profile_rescuer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rescuerHistory = ArrayList()
        victimHistory = ArrayList()
        button_logout.setOnClickListener {
            button_logout.setTextColor(resources.getColor(R.color.white_light))
            button_logout.setBackgroundResource(R.drawable.shape_bordered_button_clicked)
            firebaseAuth.signOut()
            val intent = Intent(activity, OnboardingActivity::class.java)
            startActivity(intent)
            activity!!.finishAffinity()
        }

    }

    override fun onResume() {
        super.onResume()

        val linearLayoutManager =  LinearLayoutManager(activity)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerview_history.layoutManager = linearLayoutManager

        if (role == "rescuer") {
            val ref = FirebaseDatabase.getInstance().getReference("Rescuers").child(user.uid)
            val rescuerFragment = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val rescuer = p0.getValue(Rescuer::class.java)
                    textview_profile_name.text = rescuer?.name
                    textview_profile_rescuer_division.text = rescuer?.division
                    textview_profile_rescuer_phone.text = rescuer?.phone
                    if (rescuer?.profilePhoto == "") {
                        image_profile.setImageResource(R.drawable.ic_empty_pict)
                    } else {
                        Picasso.get()
                            .load(rescuer?.profilePhoto)
                            .fit()
                            .centerCrop()
                            .placeholder(R.drawable.ic_empty_pict)
                            .error(R.drawable.ic_empty_pict)
                            .into(image_profile)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.wtf("ProfileFragmentError : ", p0.message)
                }

            }

            ref.addListenerForSingleValueEvent(rescuerFragment)


            recyclerview_history.adapter = historyRescuerAdapter
            recyclerview_history.isNestedScrollingEnabled = false

            if (rescuerHistory.isEmpty()) {
                getRescuerHistory()
            }

            button_profile_editprofile.setOnClickListener {
                val intent = Intent(activity, EditProfileRescuerActivity::class.java)
                startActivity(intent)
            }
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("AkunKorban").child(user.uid)
            val korbanFragment = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val korban = p0.getValue(AkunKorban::class.java)
                    textview_profile_name.text = korban?.name
                    textview_profile_rescuer_division.text = korban?.phone
                    if (korban?.profilePhoto == "") {
                        image_profile.setImageResource(R.drawable.ic_empty_pict)
                    } else {
                        Picasso.get()
                            .load(korban?.profilePhoto)
                            .fit()
                            .centerCrop()
                            .placeholder(R.drawable.ic_empty_pict)
                            .error(R.drawable.ic_empty_pict)
                            .into(image_profile)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.wtf("ProfileFragmentError : ", p0.message)
                }

            }

            ref.addListenerForSingleValueEvent(korbanFragment)

            recyclerview_history.adapter = historyVictimAdapter
            recyclerview_history.isNestedScrollingEnabled = false

            if (victimHistory.isEmpty()) {
                getVictimHistory()
            }

            button_profile_editprofile.setOnClickListener {
                val intent = Intent(activity, EditProfileKorbanActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getRescuerHistory() {
        val idRescuer = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().getReference("KorbanTertolong")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        if (it.child("idRescuer").value.toString() == idRescuer) {
                            val isRescuerArrived =
                                it.child("rescuerArrived").value.toString().toBoolean()
                            if (isRescuerArrived) {
                                val helpedVictim = it.getValue(KorbanTertolong::class.java)
                                rescuerHistory.add(helpedVictim!!)
                            }
                        }
                    }

                    historyRescuerAdapter.setHelpedVictim(rescuerHistory)
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("RescuerHistory", p0.message)
                }
            })
    }

    private fun getVictimHistory() {
        val victimId = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().getReference("InfoKorban")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var index = 0
                    p0.children.forEach { infoVictim ->
                        if (infoVictim.child("idKorban").value.toString() == victimId) {
                            index++
                            val infoVictimId = infoVictim.key.toString()
                            getHelpedVictim(infoVictimId)
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("Victim History Error", p0.message)
                }
            })
    }

    private fun getHelpedVictim(infoVictimId: String) {
        FirebaseDatabase.getInstance().getReference("KorbanTertolong")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        if (it.child("idInfoKorban").value.toString() == infoVictimId) {
                            val isRescuerArrived =
                                it.child("rescuerArrived").value.toString().toBoolean()
                            if (isRescuerArrived) {
                                val helpedVictim = it.getValue(KorbanTertolong::class.java)
                                victimHistory.add(helpedVictim!!)
                                historyVictimAdapter.setHistory(victimHistory)
                            }
                            return
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("Victim History Error", p0.message)
                }
            })
    }
}
