package com.example.chatapp.Notifications

class Data {

    private var user: String = ""
    private var icon = 0
    private var title: String = ""
    private var body: String = ""
    private var sented: String = ""


    constructor()
    constructor(user: String, icon: Int, title: String, body: String, sented: String) {
        this.user = user
        this.icon = icon
        this.title = title
        this.body = body
        this.sented = sented
    }


    public fun setUser(user: String) {
        this.user = user
    }

    public fun getUser(): String {
        return user;
    }

    public fun setIcon(icon: Int) {
        this.icon = icon
    }

    public fun getIcon(): Int {
        return icon;
    }

    public fun setBody(body: String) {
        this.body = body
    }

    public fun getBody(): String {
        return body;
    }

    public fun setTitle(title: String) {
        this.title = title

    }

    public fun getTitle(): String {
        return title;
    }

    public fun setSented(sented: String) {
        this.sented = sented
    }

    public fun getSented(): String {
        return sented
    }
}