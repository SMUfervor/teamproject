package com.example.schedulemanagement

import android.renderscript.RenderScript
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulemanagement.databinding.TasklistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class ListAdapter (
    private val TaskList: MutableList<com.example.schedulemanagement.TaskList>,
    private val activity: ListActivity) : RecyclerView.Adapter<ListAdapter.viewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    inner class viewHolder(private val binding: TasklistBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(list : com.example.schedulemanagement.TaskList){
            val alarmdate = list.alarm
            val deaddate = list.deadline
            val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH")
            val alarmString = dateFormat.format(alarmdate)
            val deadString = dateFormat.format(deaddate)
            val priority = list.priority
            binding.mainTitle.text = list.title
            if(alarmString=="2200.05.25.01"){
                binding.textAlarm.visibility = View.GONE
            }
            else{
                binding.textAlarm.text = "알림일 : ${alarmString}"
            }
            if(deadString=="2200.05.25.01"){
                binding.textDeadline.visibility = View.GONE
            }
            else{
                binding.textDeadline.text = "마감일 : ${deadString}"
            }
            if(priority == 100){
                binding.textPir.visibility = View.GONE
            }
            else{
                binding.textPir.text = "우선순위 : ${priority}"
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