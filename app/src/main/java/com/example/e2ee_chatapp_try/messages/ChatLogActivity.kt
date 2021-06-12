package com.example.e2ee_chatapp_try.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.e2ee_chatapp_try.NewMessageActivity
import com.example.e2ee_chatapp_try.R
import com.example.e2ee_chatapp_try.models.ChatMessage
import com.example.e2ee_chatapp_try.models.User
import com.example.e2ee_chatapp_try.views.ChatFromItem
import com.example.e2ee_chatapp_try.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerViewChatLog.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username

        //load history message
        listenForMessage()

        btnSendMessageChatLog.setOnClickListener {
            Log.d(TAG, "Attempt to send message...")
            performSendMessage()
            editTextChatLog.text.clear()
        }
    }

    private fun performSendMessage() {
        //send message to firebase
        val text = editTextChatLog.text.toString()
        val toId = toUser?.uid
        val fromId = FirebaseAuth.getInstance().uid
        if (fromId == null) return
        val ref =
            FirebaseDatabase.getInstance().getReference("/user-messages/${fromId}/${toId}").push()
        val toRef =
            FirebaseDatabase.getInstance().getReference("/user-messages/${toId}/${fromId}").push()
        val chatMess =
            ChatMessage(ref.key!!, text, fromId, toId!!, System.currentTimeMillis() / 1000)
        ref.setValue(chatMess).addOnSuccessListener {
            Log.d(TAG, "Saved our chat message: ${ref.key}")
            recyclerViewChatLog.scrollToPosition(adapter.itemCount-1)
        }
        toRef.setValue(chatMess)

        val latestMessRef = FirebaseDatabase.getInstance().getReference("/latest-messages/${fromId}/${toId}")
        latestMessRef.setValue(chatMess)
        val latestMessToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/${toId}/${fromId}")
        latestMessToRef.setValue(chatMess)
    }

    private fun listenForMessage() {
        val toId = toUser?.uid
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/${fromId}/${toId}")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                Log.d("AAA","${chatMessage?.text}")
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val fromUser = LatestMessageActivity.currentLoginUser
                        adapter.add(ChatFromItem(chatMessage.text, fromUser!!))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}
