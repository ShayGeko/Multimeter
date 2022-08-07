package com.untitled.multimeter.services.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.R
import com.untitled.multimeter.experimentdetails.ExperimentDetailsActivity

class MultimeterMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")


        val intent = Intent(this, ExperimentDetailsActivity::class.java)
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            val experimentId = remoteMessage.data["experiment_id"]

            Log.d(TAG, "Message data payload: $experimentId")
            intent.putExtra("id", experimentId)

        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)


        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_invite)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            notificationBuilder
                .setContentTitle(it.title)
                .setContentText(it.body)
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "collaboration invite",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    companion object {
        private const val TAG = MultimeterApp.APPLICATION_TAG
    }

//    internal class MyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
//        override fun doWork(): Result {
//            // TODO(developer): add long running task here.
//            return Result.success()
//        }
//    }
}