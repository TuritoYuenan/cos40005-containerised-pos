package containerised.pos

interface RealtimeServiceController {
	fun start()
	fun stop()
}

expect val realtimeServiceController: RealtimeServiceController
