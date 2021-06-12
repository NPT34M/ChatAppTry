package com.example.e2ee_chatapp_try.models

class UserAddUser(val fromUser:String,val toUser:String, val accepted: Boolean) {
    constructor():this("","",false)
}