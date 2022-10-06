package com.rtm.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.rtm.firebase.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    lateinit var database: FirebaseDatabase
    lateinit var mRef: DatabaseReference
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        mRef = database.reference.child("text")

        binding.btnSend.setOnClickListener {
            val text = binding.edText.text.toString()
            mRef.setValue(text)
        }
        mRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val readText = snapshot.value
                binding.textview.text = readText.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
}