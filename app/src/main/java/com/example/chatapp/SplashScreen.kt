package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashScreen : AppCompatActivity() {
    private var firebaseUser: FirebaseUser?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            firebaseUser = FirebaseAuth.getInstance().currentUser

            if (firebaseUser!=null){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()

            }
            else{
                val intent = Intent(this,WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }

        },3000)
    }
}