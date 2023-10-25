package com.example.chatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.models.Users

class SearchAdapter(
    private var listener: SearchItemClicked,
    private var mUsers: List<Users>,
):RecyclerView.Adapter<SearchAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.search_item,parent,false)
        val viewHolder = ViewHolder(view)
        view.setOnClickListener {
            listener.onItemClicked(mUsers[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUsers[position]
        holder.searchUsername.text = user.username
        holder.searchUserID.text = user.email
        Glide.with(holder.searchProfileImage.context).load(user.profile).circleCrop().into(holder.searchProfileImage)

    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var searchProfileImage:ImageView = itemView.findViewById(R.id.search_profile)
        var searchUsername:TextView = itemView.findViewById(R.id.search_username)
        var searchUserID:TextView = itemView.findViewById(R.id.search_userID)
    }
}

interface SearchItemClicked{
    fun onItemClicked(item:Users)
}