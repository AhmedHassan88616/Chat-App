package com.example.chatapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.TokenWatcher
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Notifications.*
import com.example.chatapp.adapters.ChatsAdapter
import com.example.chatapp.fragments.APIService
import com.example.chatapp.model.Chat
import com.example.chatapp.model.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MessageChatActivity : AppCompatActivity() {

    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? = null
    var chatsAdapterView: ChatsAdapter? = null
    var mChatList: List<Chat>? = null
    lateinit var recycler_view_chats: RecyclerView

    var reference: DatabaseReference? = null

    var notify = false

    var apiService: APIService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        val toolbar: Toolbar = findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
           
            finish()
        }

        apiService =
            Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id").toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        recycler_view_chats = findViewById(R.id.recycler_view_chats)
        recycler_view_chats.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recycler_view_chats.layoutManager = linearLayoutManager


        reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Users? = snapshot.getValue(Users::class.java)

                user_name_mChat.text = user!!.getUserName()
                Picasso.get().load(user.getProfile()).into(profile_image_mChat)

                retrieveMessages(firebaseUser!!.uid, userIdVisit, user.getProfile())
            }

        })

        send_message_btn.setOnClickListener {
            notify = true
            val message = text_message.text.toString()

            if (message == "") {
                Toast.makeText(this, "Please write a message first ...", Toast.LENGTH_SHORT).show()
            } else {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
            text_message.setText("")
        }

        attach_image_file_btn.setOnClickListener {
            notify = true
            var intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "pick image"), 430)
        }

        seenMessage(userIdVisit)
    }


    private fun sendMessageToUser(senderId: String, receiverId: String, message: String) {
        var reference = FirebaseDatabase.getInstance().reference
        var messageKey = reference.push().key
        var messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["receiver"] = receiverId
        messageHashMap["message"] = message
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        reference.child("Chats").child(messageKey!!).setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatListReference = FirebaseDatabase.getInstance().reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)

                    chatListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                chatListReference.child("id").setValue(userIdVisit)
                            }
                            val chatListReceiverRef = FirebaseDatabase.getInstance().reference
                                .child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)
                            chatListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }

                    })

                }
            }


        // implement push notification using fcm

        val usersReference = FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)

        usersReference.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                if (notify) {
                    sendNotification(receiverId, user!!.getUserName(), message)
                }
                notify = false
            }

        })
    }

    private fun sendNotification(receiverId: String, userName: String, message: String) {

        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")

        val query = ref.orderByKey().equalTo(receiverId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for (dataSnapshot in snapshot.children) {
                    val token: Token? = dataSnapshot.getValue(Token::class.java)

                    val data: Data = Data(
                        firebaseUser!!.uid,
                        R.mipmap.ic_launcher,
                        "$userName: $message",
                        "New Message",
                        userIdVisit
                    )
                    val sender = Sender(data!!, token!!.getToken())

                    apiService!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200) {
                                    if (response.body()!!.success != 1) {
                                        Toast.makeText(
                                            this@MessageChatActivity,
                                            "Failed nothing happen.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                TODO("Not yet implemented")
                            }

                        })
                }
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 430 && resultCode == Activity.RESULT_OK && data!!.data != null) {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Image is uploading.....")
            progressBar.show()


            var fileuri = data!!.data
            var storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            var messageId = ref.push().key
            var filePath = storageReference.child("$messageId.jpg")


            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileuri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it

                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                var downloadurl = task.result
                var url = downloadurl.toString()

                var messageHashMap = HashMap<String, Any?>()
                messageHashMap["sender"] = firebaseUser!!.uid
                messageHashMap["receiver"] = userIdVisit
                messageHashMap["message"] = "you sent an image."
                messageHashMap["isseen"] = false
                messageHashMap["url"] = url
                messageHashMap["messageId"] = messageId

                ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            progressBar.dismiss()
                            // implement push notification using fcm

                            val reference = FirebaseDatabase.getInstance().reference
                                .child("Users").child(firebaseUser!!.uid)

                            reference.addValueEventListener(object : ValueEventListener {

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val user = snapshot.getValue(Users::class.java)
                                    if (notify) {
                                        sendNotification(
                                            userIdVisit,
                                            user!!.getUserName(),
                                            "you sent an image."
                                        )
                                    }
                                    notify = false
                                }

                            })
                        }
                    }

            }
        }

    }


    private fun retrieveMessages(senderId: String, receiverId: String, receiverImageUrl: String) {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mChatList as ArrayList).clear()
                for (snapshot in snapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(senderId) && chat!!.getSender()
                            .equals(receiverId)
                        || chat!!.getReceiver().equals(receiverId) && chat.getSender()
                            .equals(senderId)
                    ) {
                        (mChatList as ArrayList).add(chat)
                        if (chat!!.getReceiver().equals(receiverId))
                            chat.setIsSeen(true)
                    }


                }
                chatsAdapterView = ChatsAdapter(
                    this@MessageChatActivity,
                    (mChatList as ArrayList),
                    receiverImageUrl
                )
                recycler_view_chats.adapter = chatsAdapterView
            }

        })
    }


    var seenListener: ValueEventListener? = null
    private fun seenMessage(userId: String) {
        var reference = FirebaseDatabase.getInstance().reference.child("Chats")
        seenListener = reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    var chat = dataSnapshot.getValue(Chat::class.java)

                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && chat!!.getSender()
                            .equals(userId)
                    ) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["seen"] = true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }

        })
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }

}