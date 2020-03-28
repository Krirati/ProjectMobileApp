package com.example.petlover

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.signup_btn
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        signup_btn.setOnClickListener {
            registerfun()
        }
        cancel_btn.setOnClickListener{
            findViewById<TextInputEditText>(R.id.inputEmailreg).text?.clear()
            findViewById<TextInputEditText>(R.id.inputPassreg).text?.clear()
            findViewById<TextInputEditText>(R.id.cPassreg).text?.clear()
            val intent = Intent(this, LoginActivity::class.java)
            finishAffinity();
            startActivity(intent)
        }

    }

    private fun registerfun(){
        val email = findViewById<TextInputEditText>(R.id.inputEmailreg).text.toString()
        val pass = findViewById<TextInputEditText>(R.id.inputPassreg).text.toString()
        val confirmpass = findViewById<TextInputEditText>(R.id.cPassreg).text.toString()
        val showrong =  findViewById<TextView>(R.id.showwrog)

        Log.d("RegisterActivity", email)
        Log.d("RegisterActivity",pass)
        Log.d("RegisterActivity",confirmpass)
        val checkmail: Boolean = isEmailValid(email)
        if (checkmail && confirmpass == pass && pass.length <= 6){
            showrong.visibility = View.GONE
            val db = FirebaseFirestore.getInstance()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener{
                    if(!it.isSuccessful) return@addOnCompleteListener
                    var username = usernameFromEmail(email)
                    var uid = it.result?.user?.uid
                    Log.d("RegisterActivity","Successfully ${uid}")
                    val user = hashMapOf(
                        "username" to username,
                        "email" to email,
                        "uid" to uid
                    )
                    if (uid != null) {
                        db.collection("users").document(uid).set(user)
                            .addOnSuccessListener { Log.d("RegisterActivity", "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w("RegisterActivity", "Error writing document", e) }
                    }
                    val intent = Intent(this, NavigationActivity::class.java)
                    finishAffinity();
                    startActivity(intent)
                }.addOnFailureListener{
                    var somwrong: String? = it.message
                    showrong.text = somwrong
                    showrong.visibility = View.VISIBLE
                }

        }


    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun usernameFromEmail(email: String): String {
        return if (email.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }

}
