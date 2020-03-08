package com.example.petlover

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        checkUser ()
        signin_btn.setOnClickListener {
            loginfun()
        }
        signup_btn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            finishAffinity();
            startActivity(intent)
        }
    }

    private fun loginfun(){
        val email = findViewById<TextInputEditText>(R.id.inputEmaillogin).text.toString()
        val pass = findViewById<TextInputEditText>(R.id.passlogin).text.toString()
        Log.d("login",email)
        Log.d("login",pass)
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener
                Log.d("login","Successfully login ${it.result?.user?.uid}")
                val intent = Intent(this, Bottomnavigation::class.java)
                finishAffinity();
                startActivity(intent)

            }
    }
    private fun checkUser () {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            for (profile in it.providerData) {
                // Id of the provider (ex: google.com)
                val providerId = profile.providerId

                // UID specific to the provider
                val uid = profile.uid

                // Name, email address, and profile photo Url
                val name = profile.displayName
                val email = profile.email
                val photoUrl = profile.photoUrl
//                val userProfile = User(providerId,uid,name,email)
            }

        }

        if (user != null) {
            val intent = Intent(this, Bottomnavigation::class.java)
            finishAffinity();
            startActivity(intent)
        }
    }
}
