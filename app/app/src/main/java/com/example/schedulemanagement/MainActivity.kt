package com.example.schedulemanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.schedulemanagement.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    val myDataSet = mutableListOf<MainList>()
    private lateinit var recyclerView: RecyclerView
    val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        recyclerView = viewBinding.mainRv
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = MainAdapter(myDataSet, this@MainActivity, this@MainActivity)

        viewBinding.mainPlus.setOnClickListener {
            val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme))
            builder.setTitle("목록 이름을 입력해 주세요.")

            val editText = EditText(this)
            builder.setView(editText)

            builder.setPositiveButton("목록 추가") { dialog, which ->
                if (user != null) {
                    val userId = user.uid
                    val title = editText.text.toString()
                    val currentTime = Date()
                    val newList = MainList(title, "", currentTime)
                    db.collection(userId)
                        .add(newList)
                        .addOnSuccessListener { documentReference ->
                            val documentId = documentReference.id
                            newList.ID = documentId
                            db.collection(userId)
                                .document(documentId)
                                .set(newList)
                            myDataSet.add(newList)
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "목록 추가 실패: $e", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }

            builder.setNegativeButton("취소") { dialog, which ->
                Toast.makeText(this, "목록 추가가 취소되었습니다.", Toast.LENGTH_SHORT).show()
            }

            val dialog = builder.create()
            dialog.show()
        }

        if (user != null) {
            val userId = user.uid
            loadData(userId)
        }
    }

    private fun loadData(userId : String) {
        db.collection(userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                myDataSet.clear()
                for (document in querySnapshot) {
                    val title = document.getString("title")
                    val id = document.id
                    val timestamp = document.getDate("currentTime")
                    val newList = MainList(title.toString(), id, timestamp)
                    myDataSet.add(newList)
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.logoutmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                val intent = Intent(this, LoginActivity::class.java)
                setResult(RESULT_OK, intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
