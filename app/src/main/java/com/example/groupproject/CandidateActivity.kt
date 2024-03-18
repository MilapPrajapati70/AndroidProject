package com.example.groupproject

import CandidateAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class CandidateActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CandidateAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var editTextSearch: EditText
    private lateinit var currentUser: FirebaseUser
    private lateinit var nodata: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidate)

        recyclerView = findViewById(R.id.recyclerView2)
        editTextSearch = findViewById(R.id.search)
        nodata = findViewById(R.id.nodata)


        val imgbacjk: ImageView = findViewById(R.id.imgbacjk)
        imgbacjk.setOnClickListener{
            onBackPressed()
        }

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
        })

        // Get the current user
        currentUser = FirebaseAuth.getInstance().currentUser ?: return

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("candidates")

        // Load candidates from Firebase
        loadCandidates()
    }

    private fun loadCandidates() {
        val candidatesList = mutableListOf<Candidate>()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                candidatesList.clear()
                for (postSnapshot in snapshot.children) {
                    val candidate = postSnapshot.getValue(Candidate::class.java)
                    candidate?.let {
                        // Fetch connection status from user-specific data in the database
                        val isConnected = postSnapshot.child("connections").child(currentUser.uid).exists()
                        it.isConnected = isConnected
                        candidatesList.add(it)
                    }
                }
                adapter = CandidateAdapter(candidatesList, nodata)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
