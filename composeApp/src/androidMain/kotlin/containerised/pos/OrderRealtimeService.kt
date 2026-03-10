package containerised.pos

import android.app.Service
import android.content.Intent
import android.os.IBinder

class OrderRealtimeService : Service() {

	override fun onCreate() {
		super.onCreate()

		startForeground(
			1,
			NotificationService.createForegroundNotification(
				"Order Listener",
				"Listening for new orders"
			)
		)

		OrderRealtimeManager.start()
	}

	override fun onDestroy() {
		OrderRealtimeManager.stop()
		super.onDestroy()
	}

	override fun onBind(intent: Intent?): IBinder? = null
}
