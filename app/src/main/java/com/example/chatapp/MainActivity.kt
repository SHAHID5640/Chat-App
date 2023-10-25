package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.models.Friend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity(),FriendListClicked {
    private lateinit var toolbar:Toolbar
    private lateinit var searchBtn:ImageView
    private lateinit var friendListAdapter: FriendListAdapter
    private lateinit var mFriend:List<Friend>
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Chat App"


        searchBtn = findViewById(R.id.search_btn)

        searchBtn.setOnClickListener {
            val intent = Intent(this,SearchActivity::class.java)
            startActivity(intent)
        }



        recyclerView = findViewById(R.id.friend_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)


        mFriend = ArrayList()

        retrieveAllFriends()

        val itemMargin = resources.getDimensionPixelSize(R.dimen.list_margin) // Provide the margin dimension
        val itemDecoration = ItemMarginDecoration(itemMargin)
        recyclerView.addItemDecoration(itemDecoration)


        if (!isInternetAvailable()){
            val intent = Intent(this,InternetActivity::class.java)
            intent.putExtra("Activity","main")
            startActivity(intent)
        }






    }




    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun retrieveAllFriends() {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refFriend = FirebaseDatabase.getInstance().reference.child("Friend").child(firebaseUserID)
        refFriend.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mFriend as ArrayList<Friend>).clear()
                if (snapshot.exists()){
                    for (snapshot in snapshot.children){
                        val friend: Friend? = snapshot.getValue(Friend::class.java)
                        if (friend != null) {
                            (mFriend as ArrayList<Friend>).add(friend)
                        }
                    }
                    friendListAdapter = FriendListAdapter(this@MainActivity,mFriend)
                    recyclerView.adapter = friendListAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }

            R.id.action_setting -> {
                val intent = Intent(this,SettingActivity::class.java)
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                return true
            }


        }
        return false
    }

    override fun onItemClicked(item: Friend) {
        val intent = Intent(this,ChatingActivity::class.java)
        intent.putExtra("friendUsername",item.name)
        intent.putExtra("friendID",item.uid)
        startActivity(intent)

    }

}