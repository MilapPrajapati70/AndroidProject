package com.example.groupproject


import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.groupproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DetailActivity : AppCompatActivity() {


    private lateinit var candidateName: String
    private lateinit var candidateTitle: String
    private lateinit var candidatePhotoUrl: String
    private lateinit var candidateId: String
    private var isConnected = false // Track connection status

    private lateinit var database: DatabaseReference
    private lateinit var currentUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference
        currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Retrieve candidate details from intent extras
        candidateName = intent.getStringExtra("name") ?: ""
        candidateTitle = intent.getStringExtra("title") ?: ""
        candidatePhotoUrl = intent.getStringExtra("photoUrl") ?: ""
        candidateId = intent.getStringExtra("id") ?: ""

        // Display candidate details
        val nameTextView: TextView = findViewById(R.id.name)
        val titleTextView: TextView = findViewById(R.id.title)
        val largerImageView: ImageView = findViewById(R.id.ImageView2)
        val connectButton: Button = findViewById(R.id.connectButton)

        nameTextView.text = candidateName
        titleTextView.text = candidateTitle

        // Load candidate photo with Glide
        Glide.with(this)
            .load(candidatePhotoUrl)
            .placeholder(R.drawable.s2) // Placeholder image while loading
            .error(R.drawable.s2) // Error image if loading fails
            .into(largerImageView)

        // Set initial connection status
        checkConnectionStatus(connectButton)

        // Handle connect/disconnect button click
        connectButton.setOnClickListener {
            toggleConnectionStatus(connectButton)
        }
    }

    // Function to check the connection status
    private fun checkConnectionStatus(connectButton: Button) {
        database.child("connections").child(currentUserID).child(candidateId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isConnected = snapshot.exists() && snapshot.getValue(Boolean::class.java) == true
                    updateConnectButtonText(connectButton)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    // Function to toggle connection status and update UI accordingly
    private fun toggleConnectionStatus(connectButton: Button) {
        isConnected = !isConnected
        updateConnectButtonText(connectButton)
        database.child("connections").child(currentUserID).child(candidateId).setValue(isConnected)
    }

    // Function to update connect button text based on connection status
    private fun updateConnectButtonText(connectButton: Button) {
        if (isConnected) {
            connectButton.text = "Disconnect"
        } else {
            connectButton.text = "Connect"
        }
    }
}
