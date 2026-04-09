package containerised.pos.database

import containerised.pos.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.client.call.body
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object SupabaseClient {
	private const val SUPABASE_URL = BuildConfig.SUPABASE_URL
	private const val SUPABASE_KEY = BuildConfig.SUPABASE_KEY

	private val supabase = createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
		install(Auth)
		install(Postgrest)
		install(Storage)
		install(Realtime)
		install(Functions)
	}

	val db = supabase.postgrest
	val storage = supabase.storage
	val realtime = supabase.realtime
	val auth = supabase.auth
	val functions = supabase.functions

	suspend fun uploadImage(path: String, imageBytes: ByteArray) {
		storage.from("images").upload(path, imageBytes)
	}

	@Serializable
	data class FunctionResponseESR(val url: String)

	suspend fun exportSalesReport(reportID: String): FunctionResponseESR {
		return functions.invoke<JsonObject>(
			"export-sales-report",
			buildJsonObject { put("report_id", reportID) },
			headers = Headers.build {
				append(HttpHeaders.ContentType, "application/json")
			}
		).body<FunctionResponseESR>()
	}
}
