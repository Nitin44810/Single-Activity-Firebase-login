package com.example.atom_assignment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import org.w3c.dom.Text
import java.util.*

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE: Int = 6869
    lateinit var providers : List<AuthUI.IdpConfig>
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        providers = listOf<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build(), //for email login
            AuthUI.IdpConfig.GoogleBuilder().build() //for google login
        )

        auth = FirebaseAuth.getInstance() // Initialize Firebase Auth

        if(auth.currentUser != null){
            updateUI(auth.currentUser!!)
        }else {
            signInOptions()
        }
        sign_out.setOnClickListener{
            AuthUI.getInstance().signOut(this)
                .addOnCompleteListener{
                    sign_out.isEnabled = false
                    email_text.isEnabled = false
                    signInOptions()
                }
                .addOnFailureListener {
                    e -> Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateUI(currentUser: FirebaseUser) {
        sign_out.isEnabled = true
        email_text.isEnabled = true
        email_text.text = "Hi "+currentUser.displayName
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                val user = auth.currentUser
                Toast.makeText(this,""+user!!.email,Toast.LENGTH_SHORT).show()

                sign_out.isEnabled = true
                email_text.isEnabled = true
                email_text.text = "Hi "+user.displayName
            }
            else{
                Toast.makeText(this,""+response!!.error!!.message,Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun signInOptions() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.newTheme)
            .setIsSmartLockEnabled(false,true)
            .build(),REQUEST_CODE)
    }
}