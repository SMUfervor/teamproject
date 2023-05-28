package com.example.schedulemanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
    private lateinit var ID : String
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var Alarmdate : Date
    private lateinit var Deaddate : Date
    private var myDataSet = mutableListOf<TaskList>()
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH")

    private val startListActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK){
            var Priority = 100
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, 2200)
            calendar.set(Calendar.MONTH, Calendar.DECEMBER)
            calendar.set(Calendar.DAY_OF_MONTH, 31)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
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
                val AlarmString = data?.getStringExtra("alarmString").toString()
                Alarmdate = dateFormat.parse(AlarmString)
            }
            if(DeadLine==true){
                val DeadlineString = data?.getStringExtra("deadlineString").toString()
                Deaddate = dateFormat.parse(DeadlineString)
            }

            if (user != null) {
                val userId = user.uid
                val parentDocument = db.collection(userId).document(ID)
                val newList = TaskList(title.toString(), detail.toString(), Deaddate, Priority, Alarmdate, "미완료", "", ID)

                parentDocument.collection("task list")
                    .add(newList)
                    .addOnSuccessListener { documentReference ->
                        val documentId = documentReference.id
                        newList.myid = documentId
                        parentDocument.collection("task list")
                            .document(documentId)
                            .set(newList)
                        myDataSet.add(newList)
                        recyclerView.adapter?.notifyDataSetChanged()
                        Toast.makeText(this, "작업이 추가 되었습니다.", Toast.LENGTH_SHORT)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "작업추가 실패: $e", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
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

        val intent = intent
        if (intent != null) {
            ID = intent.getStringExtra("id").toString()
            // 나머지 코드 실행
        }

        loadData(user!!.uid)

        recyclerView = viewBinding.rvList

        recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = ListAdapter(myDataSet,this@ListActivity,this@ListActivity)
    }

    private fun loadData(userId : String) {
        val parentDocument = db.collection(userId).document(ID)
        parentDocument.collection("task list")
            .get()
            .addOnSuccessListener { querySnapshot ->
                myDataSet.clear()
                for (document in querySnapshot) {
                    val title = document.getString("title")
                    val detail = document.getString("detail")
                    val deadlinet = document.getTimestamp("deadline")
                    val alarmt = document.getTimestamp("alarm")
                    val deadline: Date? = deadlinet?.toDate()
                    val alarm: Date? = alarmt?.toDate()
                    val priority = document.getLong("priority")?.toInt()
                    val complete = document.getString("complete")
                    val myid = document.getString("myid")
                    val parentid = document.getString("parentid")

                    val newList = TaskList(title.toString(), detail.toString(), deadline!!, priority!!, alarm!!, complete.toString(), myid.toString(), parentid.toString())
                    myDataSet.add(newList)
                }
                myDataSet.sortBy { it.priority }
                recyclerView.adapter?.notifyDataSetChanged()
            }
    }
}