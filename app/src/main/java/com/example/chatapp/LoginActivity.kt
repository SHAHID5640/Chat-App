package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: Toolbar
    private lateinit var loginEmail:EditText
    private lateinit var loginPassword:EditText
    private lateinit var loginBtn:Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        toolbar = findViewById(R.id.login_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Login"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loginEmail = findViewById(R.id.email_login)
        loginPassword = findViewById(R.id.password_login)
        loginBtn = findViewById(R.id.login_btn)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
        }
        progressBar =findViewById(R.id.login_Progressbar)

        auth = FirebaseAuth.getInstance()

        loginBtn.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email:String = loginEmail.text.toString()
        val password:String = loginPassword.text.toString()
        if (email ==""){
            Toast.makeText(this,"Please write Email", Toast.LENGTH_LONG).show()


        }else if (password==""){
            Toast.makeText(this,"Please write password", Toast.LENGTH_LONG).show()

        }else {
            progressBar.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressBar.visibility = View.GONE
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                }

        }
    }
}