package com.example.e2ee_chatapp_try.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profileImage: String) : Parcelable {
    constructor() : this(uid = "", username = "", profileImage = "")
}