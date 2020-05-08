package com.tugasakhir.resq.rescuer.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    companion object {
        fun newInstance(): HomeFragment =
            HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textview_tips_home.text = Html.fromHtml(getString(R.string.tips_title_home))
        button_waterlevel_beranda.setOnClickListener {
            button_waterlevel_beranda.setBackgroundResource(R.drawable.shape_bordered_button_clicked)
            button_waterlevel_beranda.setTextColor(resources.getColor(R.color.white_dark))
//            val bundle = Bundle()
//            bundle.putString("actionbar", getString(R.string.water_level_actionbar))
//            bundle.putString("link", "https://bpbd.jakarta.go.id/waterlevel")
            val intent = Intent(activity, WaterLevelActivity::class.java)
//            intent.putExtras(bundle)
            startActivity(intent)
        }

        button_buku_saku.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://siaga.bnpb.go.id/hkb/po-content/uploads/documents/Buku_Saku-10Jan18_FA.pdf"))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        button_waterlevel_beranda.setBackgroundResource(R.drawable.shape_bordered_button)
        button_waterlevel_beranda.setTextColor(resources.getColor(R.color.black_dark))
    }
}
