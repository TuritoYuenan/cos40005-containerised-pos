package containerised.pos.services

import containerised.pos.database.ChangeType

object JvmRealtimeServiceController: RealtimeServiceController {
	private var started = false

	override fun start() {
		if (started) return
		started = true

		RealtimeManager.forOrders.start(ChangeType.UPDATE)
		RealtimeManager.forIngredients.start()
		RealtimeManager.forOrderItems.start()
	}

	override fun stop() {
		RealtimeManager.forOrders.stop()
		RealtimeManager.forIngredients.stop()
		RealtimeManager.forOrderItems.stop()
		started = false
	}
}

actual val realtimeServiceController: RealtimeServiceController
	get() = JvmRealtimeServiceController
