package containerised.pos.services

import android.content.Intent
import androidx.core.content.ContextCompat
import containerised.pos.AppContextHolder

object AndroidRealtimeServiceController: RealtimeServiceController {
	private var started = false

	override fun start() {
		if (started) return
		started = true

		val context = AppContextHolder.context
		val intent = Intent(context, AndroidOrderRealtimeService::class.java)

		ContextCompat.startForegroundService(context, intent)
	}

	override fun stop() {
		val context = AppContextHolder.context
		context.stopService(Intent(context, AndroidOrderRealtimeService::class.java))
		started = false
	}
}

actual val realtimeServiceController: RealtimeServiceController
	get() = AndroidRealtimeServiceController
