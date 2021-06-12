package com.example.e2ee_chatapp_try

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.e2ee_chatapp_try.messages.LatestMessageActivity
import com.example.e2ee_chatapp_try.models.User
import com.example.e2ee_chatapp_try.models.UserAddUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.new_message_user.view.*
import java.lang.Exception

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        fetchUsers()
    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    Log.d("AAA", it.value.toString())
                    if (user != null && user.uid != LatestMessageActivity.currentLoginUser?.uid) {
                        adapter.add(UserItem(user))
                    }
//                    adapter.setOnItemClickListener { item, view ->
//
//                        val userItem = item as UserItem
//
//                        val intent = Intent(view.context, ChatLogActivity::class.java)
//                        intent.putExtra(USER_KEY, userItem.user)
//                        startActivity(intent)
//
//                        finish()
//                    }

                }
                rcvNewMessage.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}

class UserItem(val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val fromUid = LatestMessageActivity.currentLoginUser?.uid
        val toUid = user.uid
        viewHolder.itemView.txtUsernameNewMessageUser.text = user.username
        Picasso.get().load(user.profileImage).into(viewHolder.itemView.imageNewMessageUser)
        var check = false
        try {
            val references = FirebaseDatabase.getInstance().getReference("/AddFriend/${fromUid}/${toUid}")
            references.addChildEventListener(object :ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val addF = snapshot.getValue(UserAddUser::class.java)
                    if(addF!=null && addF.accepted){
                        viewHolder.itemView.btnAddFriend.text = "REMOVE"
                        check = addF.accepted
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
        }catch (e:Exception){
            Log.d("AAA","${e.message}")
        }
        viewHolder.itemView.btnAddFriend.setOnClickListener {
            if(!check){
                val ref =
                    FirebaseDatabase.getInstance().getReference("/AddFriend/${fromUid}/${toUid}").push()
                ref.setValue(UserAddUser(fromUid!!, toUid, true))
                val refTo =
                    FirebaseDatabase.getInstance().getReference("/AddFriend/${toUid}/${fromUid}").push()
                refTo.setValue(UserAddUser(toUid, fromUid!!, true))
                viewHolder.itemView.btnAddFriend.text = "REMOVE"
            }else{
                val ref =
                    FirebaseDatabase.getInstance().getReference("/AddFriend/${fromUid}/${toUid}")
                ref.removeValue()
                val refTo =
                    FirebaseDatabase.getInstance().getReference("/AddFriend/${toUid}/${fromUid}")
                refTo.removeValue()
                viewHolder.itemView.btnAddFriend.text = "ADD"
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.new_message_user
    }

}