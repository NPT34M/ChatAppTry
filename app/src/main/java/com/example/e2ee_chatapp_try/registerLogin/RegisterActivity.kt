package com.example.e2ee_chatapp_try.registerLogin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.example.e2ee_chatapp_try.messages.LatestMessageActivity
import com.example.e2ee_chatapp_try.R
import com.example.e2ee_chatapp_try.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var selectedPhotoURI: Uri? = null
        val getContentImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            selectedPhotoURI = it
            chooseAvatar.setImageURI(it)
        }

        btnRegister.setOnClickListener {
            registerUser(selectedPhotoURI.toString())
        }

        chooseAvatar.setOnClickListener {
            Log.d("ChoosePhoto", "Try to show photo selector")
            getContentImage.launch("image/*")
        }

        //Switch to Login screen
        txtvAlreadyAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun returnPhotoURI(uri: String) {
        return
    }

    private fun registerUser(uri: String) {
        val email = txtEmail.text.toString()
        val password = txtPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and Password can not blank!!!", Toast.LENGTH_SHORT).show()
            return
        }

        //Firebase authentication to create new user
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
                Log.d("CreateUser", "Successfully create with uid: ${it.result?.user?.uid}")
                uploadPhotoToFirebaseStorage(uri)
            }
            .addOnFailureListener {
                Log.d("FailedCreateUser", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun uploadPhotoToFirebaseStorage(uri: String) {
        if (uri == null) return
        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/${fileName}")

        ref.putFile(uri.toUri()!!)
            .addOnSuccessListener {
                Log.d("PutPhotoSuccess", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("DownloadURL", "File location: ${it}")
                    saveUserToFirebaseRealTimeDB(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("PutPhotoFailed", "Successfully uploaded image: ${it.message}")
            }
    }

    private fun saveUserToFirebaseRealTimeDB(profileImage: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/${uid}")
        val user = User(uid, txtUsername.text.toString(), profileImage)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SaveUserSuccess", "Sucessfully save user to Firebase Database!")
                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("SaveUserFailed", "Failed to save user: ${it.message}")
            }
    }
}
