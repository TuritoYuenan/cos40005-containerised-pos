package containerised.pos

import android.content.Intent
import androidx.core.content.ContextCompat

actual object RealtimeServiceController {
	private var started = false

	actual fun start() {
		if (started) return
		started = true

		val context = AppContextHolder.context
		val intent = Intent(context, AndroidOrderRealtimeService::class.java)

		ContextCompat.startForegroundService(context, intent)
	}

	actual fun stop() {
		val context = AppContextHolder.context
		context.stopService(Intent(context, AndroidOrderRealtimeService::class.java))
		started = false
	}
}
