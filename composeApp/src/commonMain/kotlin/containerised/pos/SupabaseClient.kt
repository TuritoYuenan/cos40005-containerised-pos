package containerised.pos

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClientProvider {
	val supabase = createSupabaseClient(
		supabaseUrl = "https://qkpsqjlkjyvvoqcyrszw.supabase.co",
		supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFrcHNxamxranl2dm9xY3lyc3p3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA4NDkwNzUsImV4cCI6MjA3NjQyNTA3NX0.QCy8IeJlvalMbtO1NTpeukEFgGgsdLg034MurFwPYqA"
	) {
		install(Auth)
		install(Postgrest)
		// install(Storage)
		install(Realtime)
	}
}
