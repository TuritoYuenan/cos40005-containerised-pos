package containerised.pos

actual object RealtimeServiceController {
	private var started = false

	actual fun start() {
		if (started) return
		started = true

		OrderRealtimeManager.start()
	}

	actual fun stop() {
		OrderRealtimeManager.stop()
		started = false
	}
}
