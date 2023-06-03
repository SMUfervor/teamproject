package com.example.schedulemanagement

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulemanagement.databinding.TasklistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class ListAdapter (private val TaskList : MutableList<com.example.schedulemanagement.TaskList>,
                   private val activity: ListActivity,
                   private val context: Context
) : RecyclerView.Adapter<ListAdapter.viewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH")
    private var taskscreen = "전체"

    fun setTaskscreen(newTaskscreen: String) {
        taskscreen = newTaskscreen
        notifyDataSetChanged()
    }
    inner class viewHolder(private val binding: TasklistBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(list : com.example.schedulemanagement.TaskList){
            val priority = list.priority
            val deadline = list.deadline
            val parentId = TaskList[adapterPosition].parentid
            val myId = TaskList[adapterPosition].myid
            val deadlineString = dateFormat.format(deadline)
            binding.mainTitle.text = list.title
            if(deadlineString == "2200.12.31.00"){
                binding.textDl.text = "마감일 : 미정"
            }
            else{
                binding.textDl.text = "마감일 : ${deadlineString}"
            }

            if(priority == 100){
                binding.textPir.text = "우선순위 : 미정"
            }
            else{
                binding.textPir.text = "우선순위 : ${priority}"
            }
            if(list.complete == "완료"){
                binding.complete.visibility = View.GONE
                binding.cancel.visibility = View.VISIBLE
            }else{
                binding.complete.visibility = View.VISIBLE
                binding.cancel.visibility = View.GONE
            }
            binding.root.setOnClickListener{
                val intent = Intent(context, TaskActivity::class.java)
                intent.putExtra("myid", myId)
                intent.putExtra("parentid", parentId)
                context.startActivity(intent)
            }
            binding.complete.setOnClickListener{
                binding.complete.visibility = View.GONE
                binding.cancel.visibility = View.VISIBLE
                list.complete = "완료"
                if(user != null) {
                    val updates = hashMapOf<String, Any>("complete" to "완료")

                    db.collection(user.uid).document(parentId)
                        .collection("task list")
                        .document(myId)
                        .update(updates)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "작업이 완료 되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                            .addOnFailureListener { e ->
                                Log.d("task", "${e}")
                            }
                }
            }
            binding.cancel.setOnClickListener{
                binding.complete.visibility = View.VISIBLE
                binding.cancel.visibility = View.GONE
                if(taskscreen == "완료"){
                    TaskList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
                list.complete = "미완료"
                if(user != null) {
                    val updates = hashMapOf<String, Any>("complete" to "미완료")

                    db.collection(user.uid).document(parentId)
                        .collection("task list")
                        .document(myId)
                        .update(updates)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "작업 완료가 취소 되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.d("task", "${e}")
                        }
                }
            }

            binding.root.setOnLongClickListener {
                val builder = AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AlertDialogTheme))
                builder.setTitle("원하시는 작업을 선택하세요.")
                builder.setPositiveButton("삭제") { dialog, which ->
                    if (user != null){
                        val userId = user.uid
                        val documentId = TaskList[adapterPosition].parentid
                        val myid = TaskList[adapterPosition].myid

                        db.collection(userId).document(documentId).collection("task list").document(myid)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(activity, "작업이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(activity, "작업이 삭제되지 않았습니다.", Toast.LENGTH_SHORT).show()
                            }
                    }
                    TaskList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
                builder.setNegativeButton("편집") { dialog, which ->
                    val position = adapterPosition
                    val intent = Intent(context, WriteActivity::class.java)
                    intent.putExtra("parentId", list.parentid)
                    intent.putExtra("myId", list.myid)
                    intent.putExtra("retask", true)
                    intent.putExtra("position", position)
                    intent.putExtra("complete", list.complete)
                    (itemView.context as? ListActivity)?.startTaskActivityForResult?.launch(intent)
                }
                builder.setNeutralButton("취소") { dialog, which ->
                }
                val dialog = builder.create()
                dialog.show()
                false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.viewHolder {
        val binding = TasklistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount() = TaskList.size

    override fun onBindViewHolder(holder: ListAdapter.viewHolder, position: Int) {
        holder.bind(TaskList[position])
    }
}