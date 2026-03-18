package containerised.pos

actual object RealtimeServiceController {
	private var started = false

	actual fun start() {
		if (started) return
		started = true

		RealtimeManager.forOrders.start()
	}

	actual fun stop() {
		RealtimeManager.forOrders.stop()
		started = false
	}
}
