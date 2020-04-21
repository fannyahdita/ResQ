package com.tugasakhir.resq.rescuer.view

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.AkunKorban
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_message.*

class ChatMessageActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_message)

        val victim = intent.getSerializableExtra("victim") as AkunKorban

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = victim.name
        actionBar.elevation = 0F

        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatFromItem())

        recyclerview_chat.layoutManager = LinearLayoutManager(this)
        recyclerview_chat.adapter = adapter
    }
}

class ChatFromItem: Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.item_chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }
}

class ChatToItem: Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.item_chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }
}