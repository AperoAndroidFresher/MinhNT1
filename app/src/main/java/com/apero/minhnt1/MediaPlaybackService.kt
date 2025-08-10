package com.apero.minhnt1

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.UiContext
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService

class MediaPlaybackService : LifecycleService() {
    private lateinit var mediaPlayer: MediaPlayer
    private val binder = MusicBinder()

    inner class MusicBinder : Binder() {
        fun getService(): MediaPlaybackService = this@MediaPlaybackService
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MPS", "onCreate called")
        mediaPlayer = MediaPlayer()
    }

    fun startMusic(uri: Uri, context: Context) {
        Log.d("MPS", "startMusic: $uri")
            mediaPlayer.setDataSource(context, uri)
            mediaPlayer.start()

    }

    fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun createNotification(): Notification {
        // Define an intent that will open when the notification is tapped
        val stopIntent = Intent(this, MediaPlaybackService::class.java)
        stopIntent.action = "com.apero.minhnt1.action.STOP_FOREGROUND"
        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent,
            PendingIntent.FLAG_IMMUTABLE)
        val notificationBuilder = NotificationCompat.Builder(this, "800")
            .setContentTitle("Music Player")
            .setContentText("Playing music")
            .setSmallIcon(R.drawable.music)
            .addAction(R.drawable.pause, "Pause", pendingStopIntent)


        return notificationBuilder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "800",
                "Music Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
