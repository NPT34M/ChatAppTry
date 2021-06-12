package com.example.e2ee_chatapp_try.uiSupport

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import com.example.e2ee_chatapp_try.R

class progressBar(val activity: Activity) {
    private lateinit var dialog: AlertDialog
    fun loading(){
        val builder = AlertDialog.Builder(activity)
        dialog = builder.setView(activity.layoutInflater.inflate(R.layout.progress_bar,null)).create()
        dialog.show()
    }
    fun dismiss(){
        dialog.dismiss()
    }
}