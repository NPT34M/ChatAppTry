package com.example.e2ee_chatapp_try.views

import com.example.e2ee_chatapp_try.R
import com.example.e2ee_chatapp_try.models.ChatMessage
import com.example.e2ee_chatapp_try.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_mess_row.view.*


class LatesMessageRow(val chatMessage: ChatMessage) : Item<ViewHolder>() {
    var chatPartnerUser: User? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtLatestMess.text = chatMessage.text
        val partnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            partnerId = chatMessage.toId
        } else {
            partnerId = chatMessage.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/${partnerId}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.txtUsernameLatestMess.text = chatPartnerUser?.username
                Picasso.get().load(chatPartnerUser?.profileImage).into(viewHolder.itemView.imageLatestMess)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_mess_row
    }

}