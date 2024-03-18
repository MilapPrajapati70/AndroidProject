package com.example.groupproject;

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groupproject.Post
import com.example.groupproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private lateinit var databaseRef: DatabaseReference

    private lateinit var postsList: MutableList<Post>
    private lateinit var auth: FirebaseAuth


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostAdapter()
        recyclerView.adapter = adapter

        // Initialize Firebase database reference
        databaseRef = FirebaseDatabase.getInstance().reference.child("posts")

        auth = FirebaseAuth.getInstance()

        postsList = mutableListOf()

        // Load posts from Firebase Realtime Database
        loadPosts()
    }

    fun navigateToCandidates(view: View) {
        val intent = Intent(this, CandidateActivity::class.java)
        startActivity(intent)
    }

    private fun loadPosts() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postsList.clear()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let { postsList.add(it) }
                }
                adapter.submitList(postsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Log.e(TAG, "Error loading posts", error.toException())
            }
        })
    }

    fun logout(view: View) {
        // Sign out the current user
        auth.signOut()

        // Navigate back to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}

