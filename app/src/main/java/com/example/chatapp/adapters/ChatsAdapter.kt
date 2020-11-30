package com.example.chatapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.ViewFullImageActivity
import com.example.chatapp.model.Chat
import com.example.chatapp.model.ChatList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.message_item_left.view.*

class ChatsAdapter(
    mContext: Context,
    mChatList: List<Chat>,
    imageUrl: String
) : RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {

    private var mContext: Context
    private var mChatList: List<Chat>
    private var imageUrl: String
    private var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {
        this.mContext = mContext
        this.mChatList = mChatList
        this.imageUrl = imageUrl
    }


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ChatsViewHolder {
        return if (position == 1) {
            var view: View =
                LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ChatsViewHolder(view)
        } else {
            var view: View =
                LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ChatsViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        var chat: Chat = mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profile_image_message)

        // image messages
        if (chat.getMessage().equals("you sent an image.") && !chat.getUrl().equals("")) {
            // image message - right side
            if (chat.getSender().equals(firebaseUser!!.uid)) {
                holder.show_text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.right_image_view)

                holder.right_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full image",
                        "Delete image",
                        "Cancel"
                    )
                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("what do you want ?")

                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.getUrl())
                            mContext.startActivity(intent)
                        } else if (which == 1) {
                            deleteSentMessage(position, holder)

                        }
                    })
                    builder.show()
                }

                // image message - left side
            } else if (!chat.getSender().equals(firebaseUser!!.uid)) {
                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.left_image_view)

                holder.left_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full image",
                        "Cancel"
                    )
                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("what do you want ?")

                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.getUrl())
                            mContext.startActivity(intent)
                        }
                    })
                    builder.show()
                }

            }
        }
        // text messages
        else {
            holder.show_text_message!!.text = chat.getMessage()
            if (firebaseUser!!.uid == chat.getSender()) {
                holder.show_text_message!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Delete Message",
                        "Cancel"
                    )
                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("what do you want ?")

                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            deleteSentMessage(position, holder)

                        }
                    })
                    builder.show()
                }
            }
        }

        // sent and seen message
        if (position == mChatList.size - 1) {
            if (chat.getIsSeen()) {
                holder.text_seen!!.text = "seen"

                if (chat.getMessage().equals("you sent an image.") && !chat.getUrl().equals("")) {
                    val lp: RelativeLayout.LayoutParams? =
                        holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }
            } else {
                holder.text_seen!!.text = "sent"

                if (chat.getMessage().equals("you sent an image.") && !chat.getUrl().equals("")) {
                    val lp: RelativeLayout.LayoutParams? =
                        holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }
            }
        } else {
            holder.text_seen!!.visibility = View.GONE
        }
    }


    class ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile_image_message: CircleImageView? = null
        var show_text_message: TextView? = null
        var left_image_view: ImageView? = null
        var text_seen: TextView? = null
        var right_image_view: ImageView? = null

        init {
            profile_image_message = itemView.findViewById(R.id.profile_image_message)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)

        }

    }

    override fun getItemViewType(position: Int): Int {

        return if (mChatList[position].getSender().equals(firebaseUser!!.uid)) {
            1
        } else {
            0
        }

    }

    private fun deleteSentMessage(position: Int, holder: ChatsViewHolder) {
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList.get(position).getMessageId())
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(holder.itemView.context, "Deleted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        "Failed, Not Deleted",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}