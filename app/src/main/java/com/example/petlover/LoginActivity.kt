package com.example.petlover

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        signin_btn.setOnClickListener {
            loginfun()
        }
        signup_btn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            finishAffinity();
            startActivity(intent)
        }
        sign_in_google.setOnClickListener {
//            signInGoogle()
        }
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }
    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this, NavigationActivity::class.java)
            finish()
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
                val intent = Intent(this, NavigationActivity::class.java)
                finish()
                startActivity(intent)

            }.addOnFailureListener{
                var somwrong: String? = it.message
                showwronglogin.text = somwrong
                showwronglogin.visibility = View.VISIBLE}
    }

}
