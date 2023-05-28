package com.example.schedulemanagement

import android.content.Context
import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulemanagement.databinding.TasklistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListAdapter (private val TaskList : MutableList<com.example.schedulemanagement.TaskList>,
                   private val activity: ListActivity,
                   private val context: Context
) : RecyclerView.Adapter<ListAdapter.viewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    inner class viewHolder(private val binding: TasklistBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(list : com.example.schedulemanagement.TaskList){
            val priority = list.priority
            binding.mainTitle.text = list.title

            if(priority == 100){
                binding.textPir.text = "우선순위 : 미정"
            }
            else{
                binding.textPir.text = "우선순위 : ${priority}"
            }
            binding.root.setOnClickListener{
                val intent = Intent(context, TaskActivity::class.java)
                intent.putExtra("myid", TaskList[adapterPosition].myid)
                intent.putExtra("parentid", TaskList[adapterPosition].parentid)
                context.startActivity(intent)
            }

            binding.root.setOnLongClickListener {
                val builder = AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AlertDialogTheme))
                builder.setTitle("작업을 삭제하시겠습니까?")
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
                builder.setNegativeButton("취소") { dialog, which ->
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