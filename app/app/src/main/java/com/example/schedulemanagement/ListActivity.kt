package com.example.schedulemanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.schedulemanagement.databinding.ActivityListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ListActivity : AppCompatActivity(){

    private lateinit var viewBinding: ActivityListBinding
    private lateinit var recyclerView: RecyclerView
    private val user = FirebaseAuth.getInstance().currentUser
    val myDataSet = mutableListOf<TaskList>()
    val db = FirebaseFirestore.getInstance()
    var Priority = 100
    lateinit var Alarmdate : Date
    lateinit var Deaddate : Date
    val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH")

    private val startListActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK){
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, 2200)
            Alarmdate = calendar.time
            Deaddate = calendar.time

            val data = result.data
            val title = data?.getStringExtra("title")
            val detail = data?.getStringExtra("detail")
            val Al = data?.getBooleanExtra("Alarm", false)
            val DeadLine = data?.getBooleanExtra("DeadLine", false)
            val Pri = data?.getBooleanExtra("Priority", false)
            if(Pri==true){
                Priority = data?.getIntExtra("Pri", 0) ?: 0
            }
            if(Al==true){
                var AlarmString = data?.getStringExtra("alarmString").toString()
                Alarmdate = dateFormat.parse(AlarmString)
            }
            if(DeadLine==true){
                var DeadlineString = data?.getStringExtra("deadlineString").toString()
                Deaddate = dateFormat.parse(DeadlineString)
            }
            myDataSet.add(TaskList(title.toString(), detail.toString(), Deaddate, Priority, Alarmdate, false))
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityListBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btnTask.setOnClickListener {
            val intent = Intent(this, WriteActivity::class.java)
            startListActivityForResult.launch(intent)
        }

        recyclerView = viewBinding.rvList

        recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = ListAdapter(myDataSet, this@ListActivity)
    }
}