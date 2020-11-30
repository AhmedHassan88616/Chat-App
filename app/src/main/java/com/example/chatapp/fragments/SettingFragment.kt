package com.example.chatapp.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.chatapp.R
import com.example.chatapp.model.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.fragment_setting.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private var userReference: DatabaseReference? = null
private var firebaseUser: FirebaseUser? = null
private var REQUEST_CODE = 7001
private var imageUri: Uri? = null
private var storageRef: StorageReference? = null
private var coverChecker: String? = ""
private var socialChecker: String? = ""


/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_setting, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")


        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (null != context) {
                        view.user_name_settings.text = user!!.getUserName()
                        Picasso.get().load(user!!.getProfile()).into(view.profile_image_settings)
                        Picasso.get().load(user!!.getCover()).into(view.cover_image_settings)
                    }
                }
            }

        })

        view.profile_image_settings.setOnClickListener {
            pickImage()
        }

        view.cover_image_settings.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }

        view.set_facebook.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()
        }

        view.set_instagram.setOnClickListener {
            socialChecker = "instagram"
            setSocialLinks()
        }

        view.set_website.setOnClickListener {
            socialChecker = "website"
            setSocialLinks()
        }


        return view
    }

    private fun setSocialLinks() {

        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
        if (socialChecker == "website") {
            builder.setTitle("write URL: ")
        } else {
            builder.setTitle("write username: ")
        }

        val editText = EditText(context)

        if (socialChecker == "website") {
            editText.hint = "www.google.com"
        } else {
            editText.hint = "https://m.facebook.com"
        }
        builder.setView(editText)

        builder.setPositiveButton("create", DialogInterface.OnClickListener { dialog, which ->
            val str = editText.text.toString()
            if (str == "") {
                Toast.makeText(context, "please enter an email !", Toast.LENGTH_LONG).show()
            } else {
                saveSocialLink(str)
            }
        })

        builder.setNegativeButton("create", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })

        builder.show()
    }

    private fun saveSocialLink(str: String) {

        val socialMap = HashMap<String, Any>()

        when (socialChecker) {
            "facebook" -> {
                socialMap["facebook"] = "https://m.facebook.com/$str"

            }
            "instagram" -> {
                socialMap["instagram"] = "https://m.instagram.com/$str"

            }
            "website" -> {
                socialMap["website"] = "https://$str"

            }
        }

        userReference!!.updateChildren(socialMap).addOnCompleteListener{
            task->
            if(task.isSuccessful)
            {
                Toast.makeText(context,"updated successfully.",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun pickImage() {
        var intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data!!.data != null) {
            imageUri = data.data
            Toast.makeText(context, "Loading ...", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }


    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Image is uploading.....")
        progressBar.show()
        if (imageUri != null) {
            var fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it

                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    var downloadurl = task.result
                    var url = downloadurl.toString()
                    if (coverChecker == "cover") {
                        var mapCoverImage = HashMap<String, Any>()
                        mapCoverImage["cover"] = url
                        userReference!!.updateChildren(mapCoverImage)
                        coverChecker = ""
                    } else {
                        var mapProfileImage = HashMap<String, Any>()
                        mapProfileImage["profile"] = url
                        userReference!!.updateChildren(mapProfileImage)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}