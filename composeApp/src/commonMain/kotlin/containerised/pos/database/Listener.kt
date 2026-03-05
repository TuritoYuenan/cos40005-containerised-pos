package containerised.pos.database

import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class OrderListener(
	private val scope: CoroutineScope,
	private val onChange: (PostgresAction) -> Unit
) {
	private var channel: RealtimeChannel? = null
	private var subscribed = false
	private var initialized = false

	fun initialize() {
		if (initialized) return
		initialized = true

		channel = SupabaseClient.realtime.channel("orders-changes")
		channel?.postgresChangeFlow<PostgresAction>(schema = "public") {
			table = "orders"
		}
			?.onEach { onChange(it) }
			?.launchIn(scope)
		println("Channel created")
	}
	fun subscribe() {
		if (subscribed) return
		subscribed = true
		scope.launch {
			channel?.subscribe()
		}
		println("Channel subscribed")
	}
	fun unsubscribe() {
		subscribed = false
		scope.launch {
			channel?.unsubscribe()
			channel = null
		}
		println("Channel unsubscribed")
	}
}
