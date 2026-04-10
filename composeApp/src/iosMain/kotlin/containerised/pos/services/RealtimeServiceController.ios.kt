package containerised.pos.services

object IosRealtimeServiceController: RealtimeServiceController {
	override fun start() { println("Not supported on iOS") }
	override fun stop() { println("Not supported on iOS") }
}

actual val realtimeServiceController: RealtimeServiceController
	get() = IosRealtimeServiceController
