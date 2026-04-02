package containerised.pos.services

object WebRealtimeServiceController: RealtimeServiceController {
	override fun start() {}
	override fun stop() {}
}

actual val realtimeServiceController: RealtimeServiceController
	get() = WebRealtimeServiceController
