package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.models.Message
import com.example.chatapp.models.Time
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ItemMarginDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = margin
        outRect.right = margin
        outRect.top = margin
        outRect.bottom = margin
    }
}
class ChatingActivity : AppCompatActivity() {
    private lateinit var refUser:DatabaseReference
    private lateinit var messageSenderID:String
    private lateinit var chatingBackBtn:ImageView
    private lateinit var friendName:TextView
    private lateinit var friendImage:ImageView
    private lateinit var msgRecyclerView: RecyclerView
    private lateinit var messageList: List<Message>
    private lateinit var messageAdapter: ChatAdapter
    private lateinit var chatMessage: EditText
    private lateinit var sendBtn:ImageButton
    private lateinit var messageReceiverID:String
    private lateinit var rootRef:DatabaseReference
    private lateinit var imageUrl:String
    private lateinit var senderName:String
    private lateinit var senderImageUrl:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chating)


        chatingBackBtn = findViewById(R.id.chating_back_btn)
        friendImage = findViewById(R.id.friend_profile_image)
        friendName = findViewById(R.id.friend_user_name)
        msgRecyclerView = findViewById(R.id.Message_List)
        chatMessage = findViewById(R.id.send_msg)
        sendBtn = findViewById(R.id.send_btn)

        friendName.text = intent.getStringExtra("friendUsername")
        messageReceiverID = intent.getStringExtra("friendID").toString()
        messageSenderID = FirebaseAuth.getInstance().currentUser!!.uid
        rootRef = FirebaseDatabase.getInstance().reference

        val recRef = rootRef.child("Users").child(messageReceiverID)

        recRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    imageUrl = snapshot.child("profile").value.toString()
                    Glide.with(friendImage.context).load(imageUrl).circleCrop().into(friendImage)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        chatingBackBtn.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        messageList = ArrayList()



        val itemMargin = resources.getDimensionPixelSize(R.dimen.item_margin) // Provide the margin dimension
        val itemDecoration = ItemMarginDecoration(itemMargin)
        msgRecyclerView.addItemDecoration(itemDecoration)

        messageAdapter = ChatAdapter(messageList)
        val lln = LinearLayoutManager(this)
        lln.stackFromEnd = true
        msgRecyclerView.layoutManager = lln
        msgRecyclerView.adapter = messageAdapter


        refUser = FirebaseDatabase.getInstance().reference.child("Users").child(messageSenderID)

        refUser.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    senderName = snapshot.child("username").value.toString()
                    senderImageUrl = snapshot.child("profile").value.toString()


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



        sendBtn.setOnClickListener {
            if (!isInternetAvailable()){
                Toast.makeText(this,"no internet connection",Toast.LENGTH_SHORT).show()
            }
            else{
            sendMessage()
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }


    override fun onStart() {
        super.onStart()
        rootRef.child("Message").child(messageSenderID).child(messageReceiverID)
            .addChildEventListener(object :ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message: Message? = snapshot.getValue(Message::class.java)

                    if (message != null) {
                        (messageList as ArrayList<Message>).add(message)
                    }

                    messageAdapter.notifyDataSetChanged()
                    msgRecyclerView.smoothScrollToPosition(messageAdapter.itemCount)


                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun sendMessage(){
        val chat:String = chatMessage.text.toString()

        if(chat.isEmpty()){
            Toast.makeText(this,"Please write Message",Toast.LENGTH_SHORT).show()
        }
        else {
            chatMessage.setText("")
            val messageSenderRef = "Message/$messageSenderID/$messageReceiverID"
            val messageReceiverRef = "Message/$messageReceiverID/$messageSenderID"

            val userMessageKeyRef: DatabaseReference = rootRef.child("Message")
                .child(messageSenderID).child(messageReceiverID).push()

            val currentTime = System.currentTimeMillis()
            val time= Time()
            val messageTime = time.formatTime(currentTime)

            val messagePushID: String? = userMessageKeyRef.key

            val messageTextBody = HashMap<String, Any>()
            messageTextBody["message"] = chat
            messageTextBody["type"] = "text"
            messageTextBody["from"] = messageSenderID
            val messageBodyDetail = HashMap<String,Any>()
            messageBodyDetail["$messageSenderRef/$messagePushID"] = messageTextBody
            messageBodyDetail["$messageReceiverRef/$messagePushID"] = messageTextBody
            rootRef.updateChildren(messageBodyDetail).addOnCompleteListener { task->
                if (task.isSuccessful){

                }

            }

            val friendMessageSenderRef = "Friend/$messageSenderID/$messageReceiverID"
            val friendMessageReceiverRef = "Friend/$messageReceiverID/$messageSenderID"





           val friendSenderChatBody = HashMap<String,Any>()
            friendSenderChatBody["name"] = friendName.text
            friendSenderChatBody["uid"] = messageReceiverID

            val friendReceiverChatBody = HashMap<String,Any>()
            friendReceiverChatBody["name"] = senderName
            friendReceiverChatBody["uid"] = messageSenderID



            val friendChatBodyDetail = HashMap<String,Any>()
            friendChatBodyDetail[friendMessageSenderRef] = friendSenderChatBody
            friendChatBodyDetail[friendMessageReceiverRef] = friendReceiverChatBody

            rootRef.updateChildren(friendChatBodyDetail).addOnCompleteListener { task->
                if (task.isSuccessful){
                    Toast.makeText(this,"Sent",Toast.LENGTH_SHORT).show()
                }
            }







        }
    }


}


