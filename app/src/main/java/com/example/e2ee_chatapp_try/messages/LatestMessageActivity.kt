package com.example.e2ee_chatapp_try.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.e2ee_chatapp_try.NewMessageActivity
import com.example.e2ee_chatapp_try.R
import com.example.e2ee_chatapp_try.models.ChatMessage
import com.example.e2ee_chatapp_try.models.User
import com.example.e2ee_chatapp_try.registerLogin.LoginActivity
import com.example.e2ee_chatapp_try.uiSupport.progressBar
import com.example.e2ee_chatapp_try.views.LatesMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_message.*

class LatestMessageActivity : AppCompatActivity() {

    companion object {
        var currentLoginUser: User? = null
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)
        recyclerViewLatestMess.adapter = adapter
        recyclerViewLatestMess.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        verifyUserLoggedIn()
        fetchCurrentUserLogin()
        listenForLatestMes()
        adapter.setOnItemClickListener { item, view ->
            Log.d("AAA","You clicked item!")
            val intent =Intent(this,ChatLogActivity::class.java)
            val row = item as LatesMessageRow
            intent.putExtra(NewMessageActivity.USER_KEY,row.chatPartnerUser)
            startActivity(intent)
        }
    }

    val latestMessMap = HashMap<String, ChatMessage>()

    private fun listenForLatestMes() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/${fromId}")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMess = snapshot.getValue(ChatMessage::class.java)
                latestMessMap[snapshot.key!!] = chatMess!!
                refreshRecyclerViewLatestMess()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMess = snapshot.getValue(ChatMessage::class.java)
                adapter.add(LatesMessageRow(chatMess!!))
                refreshRecyclerViewLatestMess()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun refreshRecyclerViewLatestMess() {
        adapter.clear()
        latestMessMap.values.forEach {
            adapter.add(LatesMessageRow(it))
        }
    }

    private fun fetchCurrentUserLogin() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/${uid}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentLoginUser = snapshot.getValue(User::class.java)
                Log.d("CurrentUserLogin", "User: ${currentLoginUser?.username} logged in!")
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun verifyUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val progress = progressBar(this)
        when (item.itemId) {
            R.id.sigoutMenu -> {
                progress.loading()
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.addContactMessageMenu -> {
                progress.loading()
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}