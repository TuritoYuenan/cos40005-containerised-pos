package containerised.pos.database

import containerised.pos.BuildKonfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

object SupabaseClient {
	private val SUPABASE_URL = BuildKonfig.SUPABASE_URL
	private val SUPABASE_KEY = BuildKonfig.SUPABASE_KEY

	private val supabase = createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
		install(Auth)
		install(Postgrest)
		install(Storage)
		install(Realtime)
	}

	val db = supabase.postgrest
	val storage = supabase.storage
	val realtime = supabase.realtime
	val auth = supabase.auth

	suspend fun uploadImage(path: String, imageBytes: ByteArray) {
		storage.from("images").upload(path, imageBytes)
	}
}
