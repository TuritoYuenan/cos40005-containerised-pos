package containerised.pos

import android.app.Service
import android.content.Intent
import android.os.IBinder
import containerised.pos.database.ChangeType

class AndroidOrderRealtimeService : Service() {
	override fun onCreate() {
		super.onCreate()

		startForeground(
			1,
			NotificationService.createForegroundNotification(
				this,
				"Order Listener",
				"Listening for new orders"
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
