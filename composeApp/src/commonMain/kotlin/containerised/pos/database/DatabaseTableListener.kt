package containerised.pos.database

import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * A generic listener for database changes on a specific table.
 * This class abstracts away the details of setting up a realtime channel and subscribing to changes.
 */
class DatabaseTableListener(
	/**
	 * The database table name to listen for changes on.
	 * This will be used to create the channel name and filter the changes.
	 */
	private val tableName: String,

	/**
	 * The coroutine scope to launch the subscription in.
	 * This should be a long-lived scope, such as the one from a ViewModel or a singleton manager.
	 */
	private val scope: CoroutineScope,

	/**
	 * The callback to invoke when a change is received from the database.
	 * The [PostgresAction] contains the type of change (INSERT, UPDATE, DELETE) and the new data.
	 */
	private val onChange: (PostgresAction) -> Unit,
) {
	private var channel: RealtimeChannel? = null
	private var subscribed = false

	fun initialize() {
		channel = SupabaseClient.realtime.channel("$tableName-changes")
		channel?.postgresChangeFlow<PostgresAction>(schema = "public") {
			table = tableName
		}
			?.onEach { onChange(it) }
			?.launchIn(scope)
		println("Channel for $tableName created")
	}

	fun subscribe() {
		if (subscribed) return
		subscribed = true
		scope.launch {
			channel?.subscribe()
		}
		println("Channel for $tableName subscribed")
	}

	fun unsubscribe() {
		scope.launch {
			channel?.let { SupabaseClient.realtime.removeChannel(it) }
			channel = null
			subscribed = false
		}
		println("Channel for $tableName unsubscribed")
	}
}
