package com.example.schedulemanagement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulemanagement.databinding.TasklistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListAdapter (
    private val TaskList: MutableList<com.example.schedulemanagement.TaskList>,
    private val activity: ListActivity) : RecyclerView.Adapter<ListAdapter.viewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    inner class viewHolder(private val binding: TasklistBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(list : com.example.schedulemanagement.TaskList){

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