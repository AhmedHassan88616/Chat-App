package com.example.chatapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.MessageChatActivity
import com.example.chatapp.R
import com.example.chatapp.VisitUserProfileActivity
import com.example.chatapp.model.Chat
import com.example.chatapp.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_search_item_layout.view.*

class UserAdapter(
    mContext: Context,
    mUsers: List<Users>,
    isChecked: Boolean
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    private var mContext: Context
    private var mUsers: List<Users>
    private var isChecked: Boolean

    var lastMsg = ""

    init {
        this.mContext = mContext
        this.mUsers = mUsers
        this.isChecked = isChecked
    }


    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImageView: ImageView
        var userNameTextView: TextView
        var onlineImageView: ImageView
        var offlineImageView: ImageView
        var lastMessageTextView: TextView

        init {
            profileImageView = itemView.findViewById(R.id.image_profile)
            userNameTextView = itemView.findViewById(R.id.user_name)
            onlineImageView = itemView.findViewById(R.id.image_online)
            offlineImageView = itemView.findViewById(R.id.image_offline)
            lastMessageTextView = itemView.findViewById(R.id.message)

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        var view: View =
            LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        var user: Users = mUsers[position]
        holder.userNameTextView.text = user.getUserName()
        Picasso.get().load(user.getProfile()).into(holder.profileImageView)


        if (isChecked) {
            retrieveLastMessage(user.getUId(), holder.lastMessageTextView)
        } else {
            holder.lastMessageTextView.visibility = View.GONE
        }

        if (isChecked) {
            if (user.getStatus() == "online") {
                holder.onlineImageView.visibility = View.VISIBLE
                holder.offlineImageView.visibility = View.GONE
            } else {
                holder.offlineImageView.visibility = View.VISIBLE
                holder.onlineImageView.visibility = View.GONE
            }
        } else {
            holder.offlineImageView.visibility = View.GONE
            holder.onlineImageView.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "send message",
                "visit profile"
            )

            var builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0) {
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.getUId())
                    mContext.startActivity(intent)

                } else if (position == 1) {
                    val intent = Intent(mContext, VisitUserProfileActivity::class.java)
                    intent.putExtra("visit_id", user.getUId())
                    mContext.startActivity(intent)
                }
            })
            builder.show()
        }
    }

    private fun retrieveLastMessage(chatUserId: String, lastMessageTextView: TextView) {
        lastMsg = "defaultMsg"

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        var reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if (firebaseUser != null && chat != null) {

                        if (chat.getReceiver() == firebaseUser!!.uid && chat.getSender() == chatUserId ||
                            chat.getSender() == firebaseUser!!.uid && chat.getReceiver() == chatUserId
                        ){
                            lastMsg=chat.getMessage()
                        }
                    }
                }
                when(lastMsg)
                {
                    "defaultMsg"->lastMessageTextView.text="No Message"
                    "sent you an image."->lastMessageTextView.text="Image sent"
                    else->lastMessageTextView.text=lastMsg
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

}