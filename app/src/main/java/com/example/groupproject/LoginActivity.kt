package com.example.groupproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize views
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        signUpButton = findViewById(R.id.SignUpButton)

        // Set click listeners
        loginButton.setOnClickListener {
            loginUser()
        }

        signUpButton.setOnClickListener {
            navigateToSignUp()
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Perform validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Authenticate user using Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    user?.uid?.let { uid ->
                        updateUI(uid)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    private fun updateUI(uid: String?) {
        if (uid != null) {
            // User is signed in, navigate to MainActivity and pass the user's UID
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
            finish()
        } else {
            // User is signed out
            // Update UI accordingly, e.g., show error message
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}





