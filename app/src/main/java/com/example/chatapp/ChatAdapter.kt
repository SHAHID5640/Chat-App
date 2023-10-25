package com.example.chatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.models.Message
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private var messageList: List<Message>) :
    RecyclerView.Adapter<ChatAdapter.MyViewHolder>()  {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent,false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
       return messageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val messages = messageList[position]
        val messageSenderID:String = FirebaseAuth.getInstance().currentUser!!.uid
        val fromUserID:String = messages.from
        val fromMessageType:String = messages.type

        if (fromMessageType=="text"){
            holder.leftText.visibility = View.GONE
            holder.rightText.visibility = View.GONE

            if(fromUserID == messageSenderID){
                holder.rightText.visibility = View.VISIBLE
                holder.rightText.text = messages.message
            }
            else{
                holder.leftText.visibility = View.VISIBLE
                holder.leftText.text = messages.message
            }
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var leftText:TextView = itemView.findViewById(R.id.left_chat)
        var rightText:TextView = itemView.findViewById(R.id.right_chat)
    }
}

