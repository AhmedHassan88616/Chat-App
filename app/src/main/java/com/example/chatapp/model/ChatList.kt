package com.example.chatapp.model

class ChatList {
    private var id: String = ""

    constructor()
    constructor(id: String) {
        this.id = id
    }


    public fun setId(id: String) {
        this.id = id
    }

    public fun getId(): String {
        return this.id
    }
}