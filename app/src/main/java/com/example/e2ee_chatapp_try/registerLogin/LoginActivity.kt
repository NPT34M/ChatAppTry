package com.example.e2ee_chatapp_try.registerLogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.e2ee_chatapp_try.messages.LatestMessageActivity
import com.example.e2ee_chatapp_try.R
import com.example.e2ee_chatapp_try.uiSupport.progressBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val progress = progressBar(this)
        btnLogin.setOnClickListener {
            val emailLogin = txtEmailLogin.text.toString()
            val passwordLogin = txtPasswordLogin.text.toString()
            if (emailLogin.isEmpty() || passwordLogin.isEmpty()) {
                Toast.makeText(this, "Email and Password can not blank!!!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            Log.d("Login Clicked", "Login with ${emailLogin}")
            progress.loading()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailLogin, passwordLogin)
                .addOnCompleteListener {
                    try {
                        Log.d("AAA","${it.result?.user}")
                        val intent = Intent(this, LatestMessageActivity::class.java)
                        startActivity(intent)
                    }catch (e:Exception){
                        Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT)
                            .show()
                        progress.dismiss()
                    }
                }
                .addOnFailureListener {
                    Log.d("LoginFailedListener","${it.message}")
                }
        }

        txtvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
//            finish()
        }
    }
}