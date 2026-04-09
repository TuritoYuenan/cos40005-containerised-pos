package containerised.pos.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import containerised.pos.database.ChangeType

class AndroidRealtimeService : Service() {
	override fun onCreate() {
		super.onCreate()

		startForeground(
			1,
			AndroidNotificationService.createForegroundNotification(
				this,
				"Realtime Service",
				"Listening for real-time updates..."
			)
		)

		RealtimeManager.forOrders.start(ChangeType.UPDATE)
		RealtimeManager.forIngredients.start()
		RealtimeManager.forOrderItems.start()
	}

	override fun onDestroy() {
		RealtimeManager.forOrders.stop()
		RealtimeManager.forIngredients.stop()
		RealtimeManager.forOrderItems.stop()
		super.onDestroy()
	}

	override fun onBind(intent: Intent?): IBinder? = null
}
