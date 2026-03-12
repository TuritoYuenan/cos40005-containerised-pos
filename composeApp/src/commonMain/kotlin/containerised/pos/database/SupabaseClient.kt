package containerised.pos.database

import containerised.pos.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

object SupabaseClient {
	private const val SUPABASE_URL = BuildConfig.SUPABASE_URL
	private const val SUPABASE_KEY = BuildConfig.SUPABASE_KEY

	private val supabase = createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
		install(Auth)
		install(Postgrest)
		install(Storage)
		install(Realtime)
	}

	val db = supabase.postgrest
	val storage = supabase.storage
	val realtime = supabase.realtime

	suspend fun uploadImage(path: String, imageBytes: ByteArray) {
		storage.from("images").upload(path, imageBytes)
	}
}
