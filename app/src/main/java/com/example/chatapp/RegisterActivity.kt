package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class RegisterActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var nameRegister:EditText
    private lateinit var emailRegister:EditText
    private lateinit var passwordRegister:EditText
    private lateinit var registerButton: Button
    private lateinit var auth : FirebaseAuth
    private lateinit var refUsers : DatabaseReference
    private var firebaseUserID :String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        toolbar = findViewById(R.id.register_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
        }
        nameRegister = findViewById(R.id.username_register)
        emailRegister = findViewById(R.id.email_register)
        passwordRegister = findViewById(R.id.password_register)
        registerButton = findViewById(R.id.register_btn)
        progressBar =findViewById(R.id.register_Progressbar)

        auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            registerUser()
        }

    }

    private fun registerUser() {
        val username:String = nameRegister.text.toString()
        val email:String = emailRegister.text.toString()
        val password:String = passwordRegister.text.toString()

        if (username ==""){
            Toast.makeText(this,"Please write username", Toast.LENGTH_LONG).show()
        }
        else if (email ==""){
            Toast.makeText(this,"Please write Email", Toast.LENGTH_LONG).show()


        }else if (password==""){
            Toast.makeText(this,"Please write password", Toast.LENGTH_LONG).show()

        }
        else{
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task->
                    if (task.isSuccessful){
                        firebaseUserID = auth.currentUser!!.uid
                        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                        val userHashMap = HashMap<String,Any>()
                        userHashMap["uid"] = firebaseUserID
                        userHashMap["username"] = username
                        userHashMap["password"] = password
                        userHashMap["email"] = email
                        userHashMap["status"] = "offline"
                        userHashMap["search"] = username.lowercase(Locale.getDefault())
                        userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/chat-app-95667.appspot.com/o/user_image.jpg?alt=media&token=58296b7c-2ada-4742-8013-99e99ba8d7a6"


                        refUsers.updateChildren(userHashMap)
                            .addOnCompleteListener{task->
                                if (task.isSuccessful){
                                    progressBar.visibility = View.GONE
                                    val intent = Intent(this,MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    }
                    else{
                        Toast.makeText(this, task.exception?.message.toString(),Toast.LENGTH_LONG).show()

                    }
                }
        }
    }

}