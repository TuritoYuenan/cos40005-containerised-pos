package containerised.pos.database

import containerised.pos.database.SupabaseClientProvider.supabase
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

val supabaseUrl = "https://qkpsqjlkjyvvoqcyrszw.supabase.co"
val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFrcHNxamxranl2dm9xY3lyc3p3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA4NDkwNzUsImV4cCI6MjA3NjQyNTA3NX0.QCy8IeJlvalMbtO1NTpeukEFgGgsdLg034MurFwPYqA"
object SupabaseClientProvider {
	val supabase = createSupabaseClient(
		supabaseUrl = supabaseUrl,
		supabaseKey = supabaseKey
	) {
		install(Auth)
		install(Postgrest)
		install(Storage)
		install(Realtime)
	}
}
suspend fun uploadImage(
	path: String,
	imageBytes: ByteArray,
) {
	val bucket = supabase.storage.from("images")
	bucket.upload(path, imageBytes)
}
