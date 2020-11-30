package com.example.chatapp.model

class Users {
    private var uId: String = ""
    private var userName: String = ""
    private var cover: String = ""
    private var email: String = ""
    private var facebook: String = ""
    private var instagram: String = ""
    private var profile: String = ""
    private var search: String = ""
    private var status: String = ""
    private var website: String = ""

    constructor()


    constructor(
        uId: String,
        userName: String,
        cover: String,
        email: String,
        facebook: String,
        instagram: String,
        profile: String,
        search: String,
        status: String,
        website: String
    ) {
        this.uId = uId
        this.userName = userName
        this.cover = cover
        this.email = email
        this.facebook = facebook
        this.instagram = instagram
        this.profile = profile
        this.search = search
        this.status = status
        this.website = website
    }


    public fun setUId(uId: String) {
        this.uId = uId
    }

    public fun getUId(): String {
        return uId
    }


    public fun setUserName(userName: String) {
        this.userName = userName
    }

    public fun getUserName(): String {
        return userName
    }

    public fun setCover(cover: String) {
        this.cover = cover
    }

    public fun getCover(): String {
        return cover
    }

    public fun setEmail(email: String) {
        this.email = email
    }

    public fun getEmail(): String {
        return email
    }


    public fun setFacebook(facebook: String) {
        this.facebook = facebook
    }

    public fun getFacebook(): String {
        return facebook
    }


    public fun setInstagram(istagram: String) {
        this.instagram = instagram
    }

    public fun getInstagram(): String {
        return instagram
    }


    public fun setProfile(profile: String) {
        this.profile = profile
    }

    public fun getProfile(): String {
        return profile
    }

    public fun setSearch(search: String) {
        this.search = search
    }

    public fun getSearch(): String {
        return search
    }


    public fun setStatus(status: String) {
        this.status = status
    }

    public fun getStatus(): String {
        return status
    }


    public fun setWebsite(website: String) {
        this.website = website
    }

    public fun getWebsite(): String {
        return website
    }

}