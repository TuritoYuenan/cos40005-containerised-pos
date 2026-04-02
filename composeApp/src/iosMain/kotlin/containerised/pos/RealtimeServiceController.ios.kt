package containerised.pos

object IosRealtimeServiceController: RealtimeServiceController {
	override fun start() {}
	override fun stop() {}
}

actual val realtimeServiceController: RealtimeServiceController
	get() = IosRealtimeServiceController
