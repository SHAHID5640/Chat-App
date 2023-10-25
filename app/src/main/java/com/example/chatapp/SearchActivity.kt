package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale


class SearchActivity : AppCompatActivity(),SearchItemClicked {
    private lateinit var searchAdapter:SearchAdapter
    private  lateinit var mUser:List<Users>
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var searchTxt:EditText
    private lateinit var backBtn:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        toolbar = findViewById(R.id.search_toolbar)
        setSupportActionBar(toolbar)
        backBtn = findViewById(R.id.back_btn)
        recyclerView = findViewById(R.id.searchList)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        mUser = ArrayList()


        backBtn.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

       searchTxt = findViewById(R.id.search_text)

        retrieveAllUsers()


        searchTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(s.toString().lowercase(Locale.ROOT))
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


    }

   private fun retrieveAllUsers() {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refusers = FirebaseDatabase.getInstance().reference.child("Users")
        refusers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUser as ArrayList<Users>).clear()
                if (searchTxt.text.toString()==""){
                    for (snapshot in snapshot.children){
                        val user:Users? = snapshot.getValue(Users::class.java)
                        if ((user!!.uid) != firebaseUserID){
                            (mUser as ArrayList<Users>).add(user)
                        }
                    }
                    searchAdapter = SearchAdapter(this@SearchActivity,mUser)
                    recyclerView.adapter = searchAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun searchForUsers(str:String){
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val queryUsers = FirebaseDatabase.getInstance().reference.child("Users")
            .orderByChild("search")
            .startAt(str)
            .endAt(str+"\uf8ff")

        queryUsers.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUser as ArrayList<Users>).clear()
                for (snapshot in snapshot.children){
                    val user:Users? = snapshot.getValue(Users::class.java)
                    if ((user!!.uid) != firebaseUserID){
                        (mUser as ArrayList<Users>).add(user)
                    }
                }
                searchAdapter = SearchAdapter(this@SearchActivity,mUser)
                recyclerView.adapter = searchAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onItemClicked(item: Users) {
        val intent = Intent(this,ChatingActivity::class.java)
        intent.putExtra("friendUsername",item.username)
        intent.putExtra("friendID",item.uid)
        startActivity(intent)
    }




}