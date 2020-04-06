package com.tugasakhir.resq.korban.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tugasakhir.resq.R
import kotlinx.android.synthetic.main.fragment_detailposko_korban.*

class PoskoDetailFragment : Fragment() {

    private lateinit var actionBar: ActionBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        actionBar = (activity as AppCompatActivity).supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.posko_actionbar)
        actionBar.elevation = 0F

        return inflater.inflate(R.layout.fragment_detailposko_korban, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tombol_petunjuk.setOnClickListener {
            val uri = "http://maps.google.com/maps?saddr=" + "-6.3302658" + "," + "106.8388629" + "&daddr=" + "-6.292016" + "," + "106.807769"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
            activity!!.finish()

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                getFragmentManager()?.popBackStackImmediate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun newInstance(): PoskoDetailFragment =
            PoskoDetailFragment()
    }
}