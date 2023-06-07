package com.example.schedulemanagement

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulemanagement.MainAdapter.viewHolder
import com.example.schedulemanagement.databinding.MainlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class MainAdapter(
    private val MainList: MutableList<com.example.schedulemanagement.MainList>,
    private val activity: MainActivity,
    private val context: Context
) : RecyclerView.Adapter<viewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    inner class viewHolder(private val binding: MainlistBinding) : RecyclerView.ViewHolder(binding.root){
        private fun cancelAlarm(childId: String) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val requestcode = childId.hashCode()
            val receiverIntent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, requestcode, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
        fun bind(list : com.example.schedulemanagement.MainList){
            binding.mainTitle.text = list.title

            binding.root.setOnLongClickListener {
                val builder = AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AlertDialogTheme))
                builder.setTitle("목록을 삭제하시겠습니까?")
                builder.setPositiveButton("삭제") { dialog, which ->
                    if (user != null){
                        val userId = user.uid
                        val documentId = MainList[adapterPosition].ID
                        val parentDocumentRef = db.collection(userId).document(documentId)

                        parentDocumentRef.collection("task list")
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val batch = db.batch()
                                for (document in querySnapshot) {
                                    val childDocRef = parentDocumentRef.collection("task list").document(document.id)
                                    val alarmday = document.getTimestamp("alarm")?.toDate()
                                    val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH")
                                    val alarm = dateFormat.format(alarmday)
                                    if(alarm != "2200.12.31.00"){
                                        cancelAlarm(document.id)
                                    }
                                    batch.delete(childDocRef)
                                }
                                batch.commit()
                                    .addOnSuccessListener {
                                        parentDocumentRef.delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(activity, "목록이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener { exception ->
                                                Toast.makeText(activity, "목록이 삭제되지 않았습니다.", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(activity, "자식 컬렉션 문서 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(activity, "자식 컬렉션 문서를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
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
                val intent = Intent(context, ListActivity::class.java)
                intent.putExtra("id", MainList[adapterPosition].ID)
                context.startActivity(intent)
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