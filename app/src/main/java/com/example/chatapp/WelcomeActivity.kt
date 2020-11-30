package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

   var firebaseUser:FirebaseUser?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        register_welcome_btn.setOnClickListener{
            var intent=Intent(this@WelcomeActivity,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }


        login_welcome_btn.setOnClickListener{
            var intent=Intent(this@WelcomeActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }




    }

    override fun onStart() {
        super.onStart()
        firebaseUser=FirebaseAuth.getInstance().currentUser
        if(null!=firebaseUser)
        {
            var intent=Intent(this@WelcomeActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}