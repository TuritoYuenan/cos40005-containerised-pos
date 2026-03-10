import android.content.Intent
import androidx.core.content.ContextCompat
import containerised.pos.AppContextHolder
import containerised.pos.OrderRealtimeService

actual object RealtimeServiceController {

	private var started = false

	actual fun start() {
		if (started) return
		started = true

		val context = AppContextHolder.context
		val intent = Intent(context, OrderRealtimeService::class.java)

		ContextCompat.startForegroundService(context, intent)
	}

	actual fun stop() {
		val context = AppContextHolder.context
		context.stopService(Intent(context, OrderRealtimeService::class.java))
		started = false
	}
}
