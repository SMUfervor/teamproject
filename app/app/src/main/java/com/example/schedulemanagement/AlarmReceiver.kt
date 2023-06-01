package com.example.schedulemanagement

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private var childId = ""
    private var parentId = ""
    private var ttitle = ""
    private var ltitle = ""


    private var CHANNEL_ID = ""
    private val CHANNEL_NAME = "작업 알림"

    override fun onReceive(context: Context, intent: Intent) {
        parentId = intent.getStringExtra("parentId").toString()
        childId = intent.getStringExtra("childId").toString()
        CHANNEL_ID = childId
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            )
            NotificationCompat.Builder(context, CHANNEL_ID)
        } else {
            NotificationCompat.Builder(context)
        }

        if (user != null) {
            db.collection(user.uid).document(parentId).collection("task list")
                .document(childId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        ttitle = documentSnapshot.getString("title").toString()

                        db.collection(user.uid).document(parentId)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    ltitle = documentSnapshot.getString("title").toString()

                                    builder.setContentTitle("${ltitle} 목록의 ${ttitle} 작업의 알림 시간입니다.")

                                    val notification = builder.build()
                                    manager.notify(childId.hashCode(), notification)
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("AlarmReceiver", "Failed to retrieve parent title: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AlarmReceiver", "Failed to retrieve task title: ${e.message}")
                }
        }

        builder.setSmallIcon(R.mipmap.ic_launcher_foreground)
        builder.setAutoCancel(true)
    }
}