package com.example.rideflow.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.rideflow.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RideTrackingService : LifecycleService() {

    private val NOTIFICATION_CHANNEL_ID = "ride_tracking_channel"
    private val NOTIFICATION_ID = 1
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking()
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY
    }
    
    private fun startTracking() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        // 在实际应用中，这里会启动位置监听和运动数据收集
        lifecycleScope.launch {
            // 模拟位置追踪
            while (true) {
                // 更新位置和运动数据
                updateTrackingData()
                delay(1000) // 每秒更新一次
            }
        }
    }
    
    private fun stopTracking() {
        // 停止位置监听和数据收集
        stopForeground(true)
        stopSelf()
    }
    
    private fun updateTrackingData() {
        // 在实际应用中，这里会获取真实的位置数据和计算运动数据
        // 并通过LiveData或其他方式更新UI
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "骑行记录",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("正在骑行中")
            .setContentText("点击查看详情")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }
    
    companion object {
        const val ACTION_START = "com.example.rideflow.START_TRACKING"
        const val ACTION_STOP = "com.example.rideflow.STOP_TRACKING"
    }
}