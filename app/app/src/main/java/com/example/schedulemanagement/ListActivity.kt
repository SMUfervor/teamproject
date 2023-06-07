package com.example.schedulemanagement

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var sortstate: SharedPreferences
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var Alarmdate : Date
    private lateinit var Deaddate : Date
    private var myDataSet = mutableListOf<TaskList>()
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH")
    private var alarmManager: AlarmManager? = null
    private var alarmId = ""
    private var taskscreen = "전체"

    val startTaskActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val title = data?.getStringExtra("title")
            var detail = ""
            detail = data?.getStringExtra("detail").toString()
            val Al = data?.getBooleanExtra("Alarm", false)
            val Priority = data?.getIntExtra("Pri", 0) ?: 0
            val AlarmString = data?.getStringExtra("alarmString").toString()
            val DeadlineString = data?.getStringExtra("deadlineString").toString()
            val Deaddate = dateFormat.parse(DeadlineString)
            val parentId = data?.getStringExtra("parentId")
            val myId = data?.getStringExtra("myId")

            if(Al == true){
                Alarmdate = dateFormat.parse(AlarmString)
                setAlarm(myId!!)
            }else{
                Alarmdate = dateFormat.parse("2200.12.31.00")
            }
            if(AlarmString != "2200.12.31.00" && Al == false){cancelAlarm(myId!!)}

            val updates = hashMapOf<String, Any>(
                "title" to title!!,
                "detail" to detail,
                "alarm" to Alarmdate,
                "deadline" to Deaddate,
                "priority" to Priority
            )

            if (user != null) {
                db.collection(user.uid).document(parentId!!).collection("task list")
                    .document(myId!!)
                    .update(updates)
                loadData(user.uid)
            }
            Toast.makeText(this, "편집이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

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
                        alarmId = newList.myid
                        myDataSet.add(newList)
                        if(Al == true){setAlarm(alarmId)}
                        if(sortstate.getString("check_sort_${ID}", "우선순위") == "우선순위"){
                            myDataSet.sortBy { it.priority }
                            recyclerView.adapter?.notifyDataSetChanged()
                        }else{
                            myDataSet.sortBy { it.deadline }
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
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

        sortstate = getSharedPreferences("sort", Context.MODE_PRIVATE)

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        viewBinding.rvList.addItemDecoration(dividerItemDecoration)

        viewBinding.btnTask.setOnClickListener {
            val intent = Intent(this, WriteActivity::class.java)
            startListActivityForResult.launch(intent)
        }

        val intent = intent
        if (intent != null) {
            ID = intent.getStringExtra("id").toString()
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
                    val deadline = document.getDate("deadline")
                    val alarm = document.getDate("alarm")
                    val priority = document.getLong("priority")?.toInt()
                    val complete = document.getString("complete")
                    val myid = document.getString("myid")
                    val parentid = document.getString("parentid")

                    val newList = TaskList(title.toString(), detail.toString(), deadline!!, priority!!, alarm!!, complete.toString(), myid.toString(), parentid.toString())
                    myDataSet.add(newList)
                }
                if(sortstate.getString("check_sort_${ID}", "우선순위") == "우선순위"){
                    myDataSet.sortBy { it.priority }
                }else{
                    myDataSet.sortBy { it.deadline }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sort_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_priority -> {
                sortstate.edit()
                    .putString("check_sort_${ID}", "우선순위")
                    .apply()
                myDataSet.sortBy { it.priority }
                recyclerView.adapter?.notifyDataSetChanged()
                return true
            }
            R.id.sort_deadline -> {
                sortstate.edit()
                    .putString("check_sort_${ID}", "마감일")
                    .apply()
                myDataSet.sortBy { it.deadline }
                recyclerView.adapter?.notifyDataSetChanged()
                return true
            }
            R.id.complete_task -> {
                taskscreen = "완료"
                (recyclerView.adapter as? ListAdapter)?.setTaskscreen(taskscreen)
                myDataSet.removeIf { task ->
                    task.complete != "완료"
                }
                viewBinding.btnTask.visibility = View.GONE
                recyclerView.adapter?.notifyDataSetChanged()
                return true
            }
            R.id.all_task -> {
                taskscreen = "전체"
                (recyclerView.adapter as? ListAdapter)?.setTaskscreen(taskscreen)
                if(user != null) {
                    myDataSet.clear()
                    loadData(user.uid)
                }
                viewBinding.btnTask.visibility = View.VISIBLE
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun setAlarm(childId : String) {
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val requestcode = alarmId.hashCode()
        val receiverIntent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("childId", childId)
            putExtra("parentId", ID)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, requestcode, receiverIntent, 0)

        val calendar: Calendar = Calendar.getInstance().apply {
            time = Alarmdate
        }

        alarmManager?.set(AlarmManager.RTC, calendar.timeInMillis, pendingIntent)
    }
    private fun cancelAlarm(childId: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val requestcode = childId.hashCode()
        val receiverIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, requestcode, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}