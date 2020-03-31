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
import com.tugasakhir.resq.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.InfoKorban
import kotlinx.android.synthetic.main.fragment_temukansayaselesai_korban.*

class StatusFinishedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_temukansayaselesai_korban, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val rescuerName = arguments!!.getString("rescuerName")
        val rescuerPhone = arguments!!.getString("rescuerPhone")
        val idInfoKorban = arguments!!.getString("idInfoKorban")
        val idKorbanTertolong = arguments!!.getString("idKorbanTertolong")

        getKorbanData(idInfoKorban)

        textview_nama_rescuer.text = rescuerName
        textview_nomor_rescuer.text = rescuerPhone

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
        fun newInstance(rescuerName: String, rescuerPhone: String, idInfoKorban: String, idKorbanTertolong: String): StatusFinishedFragment {
            val args = Bundle()
            args.putString("rescuerName", rescuerName)
            args.putString("rescuerPhone", rescuerPhone)
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
                    textview_jenis_bantuan_val.text = getString(R.string.rb_evakuasi)
                } else if (p0.child("bantuanMakanan").value.toString() == "true") {
                    textview_jenis_bantuan_val.text = getString(R.string.rb_makanan)
                } else if (p0.child("bantuanMedis").value.toString() == "true") {
                    textview_jenis_bantuan_val.text = getString(R.string.rb_medis)
                }
                textview_jumlah_lansia.text =
                    Html.fromHtml(getString(R.string.number_of_elderly, p0.child("jumlahLansia").value.toString()))
                textview_jumlah_dewasa.text =
                    Html.fromHtml(getString(R.string.number_of_adults, p0.child("jumlahDewasa").value.toString()))
                textview_jumlah_anak.text =
                    Html.fromHtml(getString(R.string.number_of_children, p0.child("jumlahAnak").value.toString()))

                Log.d("INFOKORBAN JUMLAH LANSIA ", p0.child("jumlahLansia").value.toString())
                Log.d("INFOKORBAN JUMLAH DEWASA ", p0.child("jumlahDewasa").value.toString())
                Log.d("INFOKORBAN JUMLAH ANAK ", p0.child("jumlahAnak").value.toString())
                Log.d("INFOKORBAN INFO TAMBAHAN ", p0.child("infoTambahan").value.toString())

                textview_info_tambahan_val.text = p0.child("infoTambahan").value.toString()
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.wtf("ProfileFragmentError : ", p0.message)
            }

        }

        ref.addListenerForSingleValueEvent(rescuerFragment)
    }

}