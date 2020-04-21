package com.tugasakhir.resq.rescuer.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.tugasakhir.resq.R
import com.tugasakhir.resq.korban.model.AkunKorban
import com.tugasakhir.resq.rescuer.model.Chat
import com.tugasakhir.resq.rescuer.model.Rescuer
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_message.*
import kotlinx.android.synthetic.main.item_chat_from_row.view.*
import kotlinx.android.synthetic.main.item_chat_to_row.view.*

class ChatMessageVictimActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var victim: AkunKorban
    private lateinit var rescuer: Rescuer
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_message)

        rescuer = intent.getSerializableExtra("rescuer") as Rescuer
        FirebaseDatabase.getInstance()
            .getReference("AkunKorban/${FirebaseAuth.getInstance().currentUser?.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    victim = p0.getValue(AkunKorban::class.java)!!
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("ErrorChatRescuer", p0.message)
                }
            })

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = rescuer.name
        actionBar.elevation = 0F

        messageListener()

        recyclerview_chat.layoutManager = LinearLayoutManager(this)
        recyclerview_chat.adapter = adapter

        button_send.setOnClickListener {
            performSend()
        }
    }

    private fun performSend() {
        val text = edittext_chat.text.toString()
        val fromId = FirebaseAuth.getInstance().currentUser?.uid
        val toId = rescuer.id

//        val ref = FirebaseDatabase.getInstance().getReference("Messages").push()
        val ref = FirebaseDatabase.getInstance().getReference("Messages/$fromId/$toId").push()
        val toRef =
            FirebaseDatabase.getInstance().getReference("Messages/$toId/$fromId").push()
        val chat = Chat(ref.key!!, text, fromId!!, toId, System.currentTimeMillis() / 1000)

        ref.setValue(chat)
            .addOnSuccessListener {
                Log.d("SEND MESSAGES", "Saved messages ${ref.key}")
                edittext_chat.text.clear()
                recyclerview_chat.scrollToPosition(adapter.itemCount - 1)
            }

        toRef.setValue(chat)
    }

    private fun messageListener() {
        val fromId = FirebaseAuth.getInstance().currentUser?.uid
        val toId = rescuer.id
        val ref = FirebaseDatabase.getInstance().getReference("Messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chat = p0.getValue(Chat::class.java)

                if (chat != null) {
                    if (chat.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItemVictim(chat.text, victim))
                    } else {
                        adapter.add(ChatToItemVictim(chat.text, rescuer))
                    }
                }
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }
}

class ChatToItemVictim(val text: String, val user: Rescuer) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.item_chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text
        val image = viewHolder.itemView.imageview_from
        Picasso.get()
            .load(user.profilePhoto)
            .error(R.drawable.ic_empty_pict)
            .placeholder(R.drawable.ic_empty_pict)
            .into(image)
    }
}

class ChatFromItemVictim(val text: String, val user: AkunKorban) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.item_chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text
        val image = viewHolder.itemView.image_to
        Picasso.get()
            .load(user.profilePhoto)
            .error(R.drawable.ic_empty_pict)
            .placeholder(R.drawable.ic_empty_pict)
            .into(image)
    }
}