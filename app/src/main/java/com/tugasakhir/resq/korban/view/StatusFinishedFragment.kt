package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.helper.VictimInfoData
import com.tugasakhir.resq.rescuer.model.Rescuer
import kotlinx.android.synthetic.main.activity_detail_history_victim.*
import kotlinx.android.synthetic.main.fragment_temukansayaselesai_korban.*
import kotlinx.android.synthetic.main.fragment_temukansayaselesai_korban.textview_detail_victim_history_address
import kotlinx.android.synthetic.main.fragment_temukansayaselesai_korban.textview_detail_victim_history_help_type
import kotlinx.android.synthetic.main.fragment_temukansayaselesai_korban.textview_detail_victim_history_name
import kotlinx.android.synthetic.main.fragment_temukansayaselesai_korban.textview_detail_victim_history_phone
import kotlinx.android.synthetic.main.fragment_temukansayaselesai_korban.textview_history_number_of_adult_victim
import kotlinx.android.synthetic.main.fragment_temukansayaselesai_korban.textview_history_number_of_child_victim
import kotlinx.android.synthetic.main.fragment_temukansayaselesai_korban.textview_history_number_of_elderly_victim
import kotlinx.android.synthetic.main.fragment_temukansayaselesai_korban.textview_history_victim_additional_information

class StatusFinishedFragment : Fragment() {

    private lateinit var rescuer: Rescuer
    private var victimInfoData = VictimInfoData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_temukansayaselesai_korban, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rescuer = arguments!!.getSerializable("rescuer") as Rescuer
        val idInfoKorban = arguments!!.getString("idInfoKorban")
        val idKorbanTertolong = arguments!!.getString("idKorbanTertolong")

        getKorbanData(idInfoKorban)

        textview_detail_victim_history_name.text = rescuer.name
        textview_detail_victim_history_phone.text = rescuer.phone

        if (rescuer?.profilePhoto == "") {
            imageview_rescuer_pict.setImageResource(R.drawable.ic_empty_pict)
        } else {
            Picasso.get()
                .load(rescuer?.profilePhoto)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_empty_pict)
                .error(R.drawable.ic_empty_pict)
                .into(imageview_rescuer_pict)
        }


        button_selesai.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseDatabase.getInstance().reference.child("AkunKorban/$user").child("askingHelp").setValue(false)
            FirebaseDatabase.getInstance().reference.child("KorbanTertolong/$idKorbanTertolong").child("finished").setValue(true)

            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }
    }
    companion object {
        fun newInstance(rescuer: Rescuer?, idInfoKorban: String, idKorbanTertolong: String): StatusFinishedFragment {
            val args = Bundle()
            args.putSerializable("rescuer", rescuer)
            args.putString("idInfoKorban", idInfoKorban)
            args.putString("idKorbanTertolong", idKorbanTertolong)
            val fragment = StatusFinishedFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun getKorbanData(idInfoKorban: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("InfoKorban/$idInfoKorban")
        val rescuerFragment = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.child("bantuanEvakuasi").value.toString() == "true") {
                    textview_detail_victim_history_help_type.text = getString(R.string.rb_evakuasi)
                } else if (p0.child("bantuanMakanan").value.toString() == "true") {
                    textview_detail_victim_history_help_type.text = getString(R.string.rb_makanan)
                } else if (p0.child("bantuanMedis").value.toString() == "true") {
                    textview_detail_victim_history_help_type.text = getString(R.string.rb_medis)
                }
                textview_history_number_of_elderly_victim.text =
                    Html.fromHtml(getString(R.string.number_of_elderly, p0.child("jumlahLansia").value.toString()))
                textview_history_number_of_adult_victim.text =
                    Html.fromHtml(getString(R.string.number_of_adults, p0.child("jumlahDewasa").value.toString()))
                textview_history_number_of_child_victim.text =
                    Html.fromHtml(getString(R.string.number_of_children, p0.child("jumlahAnak").value.toString()))

                val address = victimInfoData.getAddress(
                    p0.child("latitude").value.toString().toDouble(),
                    p0.child("longitude").value.toString().toDouble(),
                    context!!
                )

                textview_detail_victim_history_address.text = address

                Log.d("INFOKORBAN JUMLAH LANSIA ", p0.child("jumlahLansia").value.toString())
                Log.d("INFOKORBAN JUMLAH DEWASA ", p0.child("jumlahDewasa").value.toString())
                Log.d("INFOKORBAN JUMLAH ANAK ", p0.child("jumlahAnak").value.toString())
                Log.d("INFOKORBAN INFO TAMBAHAN ", p0.child("infoTambahan").value.toString())

                textview_history_victim_additional_information.text = p0.child("infoTambahan").value.toString()
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.wtf("ProfileFragmentError : ", p0.message)
            }

        }

        ref.addListenerForSingleValueEvent(rescuerFragment)
    }

}