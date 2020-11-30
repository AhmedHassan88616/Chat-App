package com.example.chatapp.model

class Chat {

    private var sender: String = ""
    private var receiver: String = ""
    private var message: String = ""
    private var isSeen: Boolean = false
    private var url: String = ""
    private var messageId: String = ""


    constructor()

    constructor(
        sender: String,
        receiver: String,
        message: String,
        isSeen: Boolean,
        url: String,
        messageId: String
    ) {
        this.sender = sender
        this.receiver = receiver
        this.message = message
        this.isSeen = isSeen
        this.url = url
        this.messageId = messageId
    }


    public fun setSender(sender: String) {
        this.sender = sender
    }

    public fun getSender(): String {
        return this.sender
    }


    public fun setReceiver(receiver: String) {
        this.receiver = receiver
    }

    public fun getReceiver(): String {
        return this.receiver
    }


    public fun setMessage(message: String) {
        this.message = message
    }

    public fun getMessage(): String {
        return this.message
    }



    public fun setIsSeen(isSeen: Boolean) {
        this.isSeen = isSeen
    }

    public fun getIsSeen(): Boolean {
        return this.isSeen
    }


    public fun setUrl(url: String) {
        this.url = url
    }

    public fun getUrl(): String {
        return this.url
    }


    public fun setMessageId(messageId: String) {
        this.messageId = messageId
    }

    public fun getMessageId(): String {
        return this.messageId
    }

}