package com.example.chatapp.Notifications

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceId : FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var refreshToken = FirebaseInstanceId.getInstance().token

        if (firebaseUser != null) {
            updateToken(refreshToken)
        }
    }

    private fun updateToken(refreshToken: String?) {
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var ref = FirebaseDatabase.getInstance().reference
        val token = Token(refreshToken!!)

        ref.child(firebaseUser!!.uid).setValue(token)
    }
}