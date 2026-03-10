package containerised.pos

import containerised.pos.database.OrderListener
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

object OrderRealtimeManager {

	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
	private val _events = MutableSharedFlow<PostgresAction>()
	val events = _events.asSharedFlow()

	private var listener: OrderListener? = null

	fun start(onChange: (PostgresAction) -> Unit = {}) {
		println("OrderRealtimeManager.start() called")
		if (listener != null) return

		listener = OrderListener(scope) { action ->
			scope.launch {
				_events.emit(action)
				onChange(action)
			}
		}
		listener?.initialize()
		listener?.subscribe()
	}

	fun stop() {
		listener?.unsubscribe()
		listener = null
	}
}
