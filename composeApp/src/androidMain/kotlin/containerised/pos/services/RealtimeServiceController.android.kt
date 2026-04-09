package containerised.pos.services

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import containerised.pos.AppGraph

class AndroidRealtimeServiceController(
	private val context: Context
) : RealtimeServiceController {
	private var started = false

	override fun start() {
		if (started) return
		started = true

		val intent = Intent(context, AndroidRealtimeService::class.java)
		ContextCompat.startForegroundService(context, intent)
	}

	override fun stop() {
		context.stopService(Intent(context, AndroidRealtimeService::class.java))
		started = false
	}
}

actual val realtimeServiceController: RealtimeServiceController
	get() = AppGraph.realtimeServiceController
