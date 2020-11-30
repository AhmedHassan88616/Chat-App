package com.example.chatapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatapp.model.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_visit_user_profile.*

class VisitUserProfileActivity : AppCompatActivity() {
    var userVisitId = ""
    var user: Users? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profile)

        userVisitId = intent.getStringExtra("visit_id").toString()

        var ref = FirebaseDatabase.getInstance().reference.child("Users").child(userVisitId)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(Users::class.java)
                    user_name_display.text = user!!.getUserName()
                    Picasso.get().load(user!!.getProfile()).into(profile_display)
                    Picasso.get().load(user!!.getCover()).into(cover_display)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        facebook_display.setOnClickListener {
            val uri = Uri.parse(user!!.getFacebook())
            var intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        instagram_display.setOnClickListener {
            val uri = Uri.parse(user!!.getInstagram())
            var intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        website_display.setOnClickListener {
            val uri = Uri.parse(user!!.getWebsite())
            var intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        send_msg_btn.setOnClickListener {
            val intent = Intent(this@VisitUserProfileActivity, MessageChatActivity::class.java)
            intent.putExtra("visit_id", user!!.getUId())
            startActivity(intent)
        }
    }

}