package containerised.pos.services

interface RealtimeServiceController {
	fun start()
	fun stop()
}

expect val realtimeServiceController: RealtimeServiceController
