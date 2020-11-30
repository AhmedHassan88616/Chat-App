package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(findViewById(R.id.toolbar_register))


        val toolbar: Toolbar = findViewById(R.id.toolbar_register)

        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            var intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        register_btn.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val userName: String = username_edit_text_register.text.toString()
        val email: String = email_edit_text_register.text.toString()
        val password = password_edit_text_register.text.toString()

        if (userName == "") {
            Toast.makeText(this@RegisterActivity, "please write userName", Toast.LENGTH_LONG).show()
        } else if (email == "") {
            Toast.makeText(this@RegisterActivity, "please write email", Toast.LENGTH_LONG).show()

        } else if (password == "") {
            Toast.makeText(this@RegisterActivity, "please write password", Toast.LENGTH_LONG).show()
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseUserId = mAuth.currentUser!!.uid
                        refUsers = FirebaseDatabase.getInstance().reference.child("Users")
                            .child(firebaseUserId)

                        var userHashMap = HashMap<String, Any>()
                        userHashMap["uId"] = firebaseUserId
                        userHashMap["userName"] = userName
                        userHashMap["cover"] =
                            "https://firebasestorage.googleapis.com/v0/b/chatapp-9b9ce.appspot.com/o/cover.jpg?alt=media&token=36c21bf8-40ff-49e1-b071-9914b413bd90"
                        userHashMap["profile"] =
                            "https://firebasestorage.googleapis.com/v0/b/chatapp-9b9ce.appspot.com/o/profile.jpg?alt=media&token=48ccab78-577c-4b17-86d8-4dfe74b298ff"
                        userHashMap["status"] = "offline"
                        userHashMap["search"] = userName.toLowerCase()
                        userHashMap["facebook"] = "http://m.facebook.com"
                        userHashMap["instagram"] = "http://m.instagram.com"
                        userHashMap["website"] = "http://www.google.com"


                        refUsers.updateChildren(userHashMap)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    var intent=Intent(this@RegisterActivity,MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }


                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error Message: " + task.exception!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}