package containerised.pos.services

object WebRealtimeServiceController: RealtimeServiceController {
	override fun start() { println("Not supported on web") }
	override fun stop() { println("Not supported on web") }
}

actual val realtimeServiceController: RealtimeServiceController
	get() = WebRealtimeServiceController
