package com.example.schedulemanagement

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulemanagement.MainAdapter.viewHolder
import com.example.schedulemanagement.databinding.MainlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainAdapter(
    private val MainList: MutableList<com.example.schedulemanagement.MainList>,
    private val activity: MainActivity) : RecyclerView.Adapter<viewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    inner class viewHolder(private val binding: MainlistBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(list : com.example.schedulemanagement.MainList){
            binding.mainTitle.text = list.title

            binding.root.setOnLongClickListener {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("목록을 삭제하시겠습니까?")
                builder.setPositiveButton("삭제") { dialog, which ->
                    if (user != null){
                        val userId = user.uid
                        val documentId = MainList[adapterPosition].ID
                        db.collection(userId)
                            .document(documentId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(activity, "목록이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(activity, "목록이 삭제되지 않았습니다.", Toast.LENGTH_SHORT).show()
                            }
                    }
                    MainList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
                builder.setNegativeButton("취소") { dialog, which ->
                }
                val dialog = builder.create()
                dialog.show()
                false
            }

            binding.root.setOnClickListener{
                Toast.makeText(activity, "${MainList[adapterPosition].title}\n${MainList[adapterPosition].ID}\n${MainList[adapterPosition].date}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = MainlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(MainList[position])
    }

    override fun getItemCount() = MainList.size
}