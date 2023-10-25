package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class SettingActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: Toolbar
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var refusers: DatabaseReference
    private lateinit var userImage:ImageView
    private lateinit var userName:TextView
    private lateinit var userProfileImageRef:StorageReference
    private lateinit var name:String
    private lateinit var userEmail:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        toolbar = findViewById(R.id.setting_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Setting"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        userImage = findViewById(R.id.profileImage)
        userName = findViewById(R.id.userName)
        progressBar = findViewById(R.id.setting_progressBar)
        userEmail = findViewById(R.id.userEmail)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        refusers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
        userProfileImageRef = FirebaseStorage.getInstance().reference.child("Profile Images")

        refusers.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    name = snapshot.child("username").value.toString()
                    userName.text = snapshot.child("username").value.toString()
                    val imageUrl:String = snapshot.child("profile").value.toString()
                    Glide.with(userImage.context).load(imageUrl).circleCrop().into(userImage)
                    userEmail.text = snapshot.child("email").value.toString()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


        userImage.setOnClickListener {

             pickImageFromGallery()
        }


    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data

            CropImage.activity(selectedImageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            if(resultCode == Activity.RESULT_OK){
                userImage.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                val croppedImageUri = result.uri
                val storage = FirebaseStorage.getInstance()
                val storageRef = storage.reference
                val imageRef = storageRef.child("images").child("$name.jpg")

                val uploadTask = croppedImageUri?.let { imageRef.putFile(it) }

                uploadTask!!.addOnSuccessListener {
                    // Image uploaded successfully
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Now proceed to uploading metadata to the Realtime Database
                        val imageUrl = downloadUri.toString()
                        refusers.child("profile").setValue(imageUrl).addOnCompleteListener { task->
                            if (task.isSuccessful){
                                progressBar.visibility = View.GONE
                                userImage.visibility = View.VISIBLE
                                Toast.makeText(this,"Profile update successfully",Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }.addOnFailureListener {
                    // Handle upload failure
                }
            }




            }




        }
}
