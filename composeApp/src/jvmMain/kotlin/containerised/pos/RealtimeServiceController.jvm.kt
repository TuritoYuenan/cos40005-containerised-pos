package containerised.pos

import containerised.pos.database.ChangeType

actual object RealtimeServiceController {
	private var started = false

	actual fun start() {
		if (started) return
		started = true

		RealtimeManager.forOrders.start(ChangeType.UPDATE)
		RealtimeManager.forIngredients.start()
		RealtimeManager.forOrderItems.start()
	}

	actual fun stop() {
		RealtimeManager.forOrders.stop()
		RealtimeManager.forIngredients.stop()
		RealtimeManager.forOrderItems.stop()
		started = false
	}
}
