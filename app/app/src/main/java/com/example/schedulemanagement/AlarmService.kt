package com.example.schedulemanagement

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AlarmService : Service() {

    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private var childId = ""
    private var parentId = ""
    private var ttitle = ""
    private var ltitle = ""

    private var CHANNEL_ID = "Schedule"
    private var CHANNEL_NAME = "알림"

    private val STOP_SERVICE_ACTION = "STOP_SERVICE"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == STOP_SERVICE_ACTION) {
            stopForeground(true)
            stopSelf()
            deleteNotificationChannel()
            return START_NOT_STICKY
        }

        parentId = intent?.getStringExtra("parentId").toString()
        childId = intent?.getStringExtra("childId").toString()
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            )
            NotificationCompat.Builder(this, CHANNEL_ID)
        } else {
            NotificationCompat.Builder(this)
        }

        val stopServiceIntent = Intent(this, AlarmService::class.java)
        stopServiceIntent.action = STOP_SERVICE_ACTION
        val stopServicePendingIntent = PendingIntent.getService(this, 0, stopServiceIntent, 0)

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
                                    builder.setContentIntent(stopServicePendingIntent)

                                    val notification = builder.build()
                                    startForeground(childId.hashCode(), notification)
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("AlarmService", "Failed to retrieve parent title: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AlarmService", "Failed to retrieve task title: ${e.message}")
                }
        }
        builder.setSmallIcon(R.mipmap.ic_launcher_foreground)
        builder.setAutoCancel(true)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun deleteNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.deleteNotificationChannel(CHANNEL_ID)
        }
    }
}
