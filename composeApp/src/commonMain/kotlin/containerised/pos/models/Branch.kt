package containerised.pos.models

import containerised.pos.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class Branch(
	val branch_id: String? = null,
	val branch_name: String,
	val address: String? = null,
	val phone_number: String? = null,
	val email: String? = null,
	val is_active: Boolean = true,
	val created_at: String? = null
)

// Add a new branch
suspend fun addBranch(branch: Branch) {
	SupabaseClientProvider.supabase.postgrest["branches"].insert(branch)
}

// Fetch all branches
suspend fun fetchBranches(): List<Branch> {
	return SupabaseClientProvider.supabase.postgrest["branches"].select().decodeList<Branch>()
}
