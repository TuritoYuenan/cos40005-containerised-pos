package containerised.pos

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

actual object NotificationService {

	private const val CHANNEL_ID = "orders_channel"
	private var appContext: Context? = null

	fun initialize(context: Context) {
		appContext = context.applicationContext
		createChannel(context)
	}

	private fun createChannel(context: Context) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

			val channel = NotificationChannel(
				CHANNEL_ID,
				"Order Updates",
				NotificationManager.IMPORTANCE_HIGH
			).apply {
				description = "Notifications for new or updated orders"
			}

			val manager =
				context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

			manager.createNotificationChannel(channel)
		}
	}

	actual fun showNotification(
		title: String,
		message: String
	) {
		val context = appContext ?: run {
			Log.d("NOTIF", "Context is null")
			return
		}
		Log.d("NOTIF", "Showing notification")

		val builder = NotificationCompat.Builder(context, CHANNEL_ID)
			.setSmallIcon(R.drawable.icon)
			.setContentTitle(title)
			.setContentText(message)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setAutoCancel(true)

		val manager =
			context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

		manager.notify(System.currentTimeMillis().toInt(), builder.build())
	}
}
