package containerised.pos.database

import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
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

	fun subscribe() {
		if (subscribed) return
		subscribed = true

		val channel = SupabaseClient.realtime.channel("orders-changes")
		val changes = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
			table = "orders"
			filter(
				FilterOperation("status", FilterOperator.EQ, "PREPARING")
			)
		}

		changes
			.onEach {
				onChange(it)
			}
			.launchIn(scope)

		scope.launch {
			channel.subscribe()
		}
	}
	fun unsubscribe() {
		subscribed = false
		scope.launch {
			channel?.unsubscribe()
			channel = null
		}
	}
}
