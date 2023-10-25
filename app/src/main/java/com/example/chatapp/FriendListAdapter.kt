package com.example.chatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.models.Friend
import com.example.chatapp.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendListAdapter(private var listener:FriendListClicked,
    private var mFriend:List<Friend>):RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    private var mMessage:List<Message> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.friend_list,parent,false)
        val viewHolder = ViewHolder(view)
        view.setOnClickListener {
            listener.onItemClicked(mFriend[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mFriend.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mFriend.isNotEmpty()){
            val friend = mFriend[position]
            holder.friendProfileName.text = friend.name
            val refFriendImageUrl = FirebaseDatabase.getInstance().reference.child("Users").child(friend.uid)
            refFriendImageUrl.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                       val imageUrl = snapshot.child("profile").value.toString()
                        Glide.with(holder.friendProfileImage.context).load(imageUrl).circleCrop().into(holder.friendProfileImage)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            val  firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
            val refMsgList = FirebaseDatabase.getInstance().reference.child("Message").child(firebaseUserID).child(friend.uid)
            refMsgList.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    (mMessage as ArrayList<Message>).clear()
                    if (snapshot.exists()){
                        for (snapshot in snapshot.children) {
                            val message: Message? = snapshot.getValue(Message::class.java)
                            if (message!=null){
                                (mMessage as ArrayList<Message>).add(message)
                            }
                        }
                        if(mMessage.isNotEmpty()){
                            val message = mMessage[mMessage.size-1]
                            if (message.from == firebaseUserID){
                                holder.friendMessage.text = "You : ${message.message}"
                               // holder.textTime.text = message.time
                            }
                            else{
                                holder.friendMessage.text = message.message
                              //  holder.textTime.text = message.time
                            }
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })



        }
    }


    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val friendProfileImage:ImageView = itemView.findViewById(R.id.chat_list_Image)
        val friendProfileName:TextView = itemView.findViewById(R.id.chat_list_name)
        val friendMessage:TextView = itemView.findViewById(R.id.text_msg)
    }
}

interface FriendListClicked{
    fun onItemClicked(item: Friend)
}