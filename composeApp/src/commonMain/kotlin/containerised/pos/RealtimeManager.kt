package containerised.pos

import containerised.pos.database.DatabaseTableListener
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RealtimeManager(
	private val tableName: String,
	private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
	private val _events = MutableSharedFlow<PostgresAction>()
	val events: SharedFlow<PostgresAction> = _events.asSharedFlow()

	private var listener: DatabaseTableListener? = null

	fun start() {
		if (listener != null) return

		listener = DatabaseTableListener(tableName, scope) { action ->
			scope.launch { _events.emit(action) }
		}
		listener?.initialize()
		listener?.subscribe()
	}

	fun stop() {
		listener?.unsubscribe()
		listener = null
	}

	companion object {
		val forOrders = RealtimeManager("orders")
		val forIngredients = RealtimeManager("ingredients")
	}
}
