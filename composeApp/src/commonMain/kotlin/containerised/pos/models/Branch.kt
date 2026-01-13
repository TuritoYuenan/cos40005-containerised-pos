package containerised.pos.models

import containerised.pos.database.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Branch(
	@SerialName("branch_id")
	val branchId: String,

	@SerialName("branch_name")
	val branchName: String,

	@SerialName("address")
	val address: String? = null,

	@SerialName("phone_number")
	val phoneNumber: String? = null,

	@SerialName("email")
	val email: String? = null,

	@SerialName("is_active")
	val isActive: Boolean = true,

	@SerialName("created_at")
	val createdAt: String? = null
)

// Add a new branch
suspend fun addBranch(branch: Branch) {
	SupabaseClientProvider.supabase.postgrest["branches"].insert(branch)
}

// Fetch all branches
suspend fun fetchBranches(): List<Branch> {
	return SupabaseClientProvider.supabase.postgrest["branches"].select().decodeList<Branch>()
}
