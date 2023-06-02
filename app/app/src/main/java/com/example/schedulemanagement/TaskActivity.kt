package com.example.schedulemanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.schedulemanagement.databinding.ActivityTaskBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class TaskActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityTaskBinding
    private val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH")
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private var myId = ""
    private var parentId = ""
    private var userId = ""
    private var state = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        userId = user!!.uid
        parentId = intent.getStringExtra("parentid").toString()
        myId = intent.getStringExtra("myid").toString()

        db.collection(userId).document(parentId).collection("task list").document(myId)
            .get()
            .addOnSuccessListener {document ->
                if(document != null){
                    viewBinding.taskTitle.text = document.getString("title")
                    viewBinding.taskDetail.text = document.getString("detail")
                    val deadline = document.getTimestamp("deadline")?.toDate()
                    val alarm = document.getTimestamp("alarm")?.toDate()

                    val deadlinetext = dateFormat.format(deadline)
                    val alarmtext = dateFormat.format(alarm)

                    if(deadlinetext == "2200.12.31.00"){
                        viewBinding.taskDeadline.text = "마감일 : 미정"
                    }else{
                        viewBinding.taskDeadline.text = "마감일 : ${deadlinetext}"
                    }

                    if(alarmtext == "2200.12.31.00"){
                        viewBinding.taskAlarm.text = "알림일 : 미정"
                    }else{
                        viewBinding.taskAlarm.text = "알림일 : ${alarmtext}"
                    }

                    if(document.getLong("priority")?.toInt() == 100){
                        viewBinding.taskPriority.text = "우선순위 : 미정"
                    }else{
                        viewBinding.taskPriority.text = "우선순위 : ${document.getLong("priority")?.toInt()}"
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.task_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.retask -> {
                return true
            }else -> return super.onOptionsItemSelected(item)
        }
    }
}