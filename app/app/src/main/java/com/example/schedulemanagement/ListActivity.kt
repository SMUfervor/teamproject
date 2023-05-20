package com.example.schedulemanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.schedulemanagement.databinding.ActivityListBinding
import com.example.schedulemanagement.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListActivity : AppCompatActivity(){

    private lateinit var viewBinding: ActivityListBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityListBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btnTask.setOnClickListener {
            val intent = Intent(this, WriteActivity::class.java)
            startActivity(intent)
        }
    }
}