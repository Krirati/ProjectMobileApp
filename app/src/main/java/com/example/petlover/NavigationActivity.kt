package com.example.petlover

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import com.example.petlover.ui.chat.ChatFragment
import com.example.petlover.ui.home.HomeFragment
import com.example.petlover.ui.user.UserFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class NavigationActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val db = FirebaseFirestore.getInstance()
    private var firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_user,
                R.id.navigation_settings
            ), drawerLayout
        )
        updateNavHeader()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
//        var fragment: Fragment? = null
//        navView.setNavigationItemSelectedListener{ it ->
//            when (it.itemId) {
//                R.id.navigation_logout -> {
//                    logout()
//                }
//                R.id.navigation_home -> {
//                    fragment = HomeFragment()
//                }
//                R.id.navigation_notifications -> {
//                    fragment = ChatFragment()
//                }
//                R.id.navigation_user -> {
//                    fragment = UserFragment()
//                }
//                R.id.navigation_settings -> {
//                    Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
//                }
//            }
//            if (fragment != null) {
//                val transaction = supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.nav_host_fragment, fragment!!)
//                transaction.commit()
//            }
//            drawerLayout.closeDrawer(GravityCompat.START)
//            return@setNavigationItemSelectedListener true
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun logout(){
        firebaseAuth.signOut();
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun updateNavHeader() {
        if (firebaseAuth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        var firebaseAuth = FirebaseAuth.getInstance().currentUser
        val navView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navView.getHeaderView(0)
        val navUsername: TextView = headerView.findViewById(R.id.nav_username)
        val navEmail: TextView = headerView.findViewById(R.id.nav_email)
        val navImg: ImageView = headerView.findViewById(R.id.nav_img)
        if (firebaseAuth?.displayName == null) navUsername.setText(firebaseAuth?.email) else navUsername.setText(firebaseAuth?.displayName)
        navEmail.text = firebaseAuth?.email
        Picasso.get()
            .load("${firebaseAuth?.photoUrl}")
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(navImg)

    }
    private fun NavigationView.setNavigationItemSelectedListener(navigationActivity: NavigationActivity) {
        navigationActivity.supportFragmentManager
    }

}


