package containerised.pos.models

import containerised.pos.database.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BranchItem(
	@SerialName("branch_id")
	val branchId: String,

	@SerialName("item_id")
	val itemId: String,

	@SerialName("category_id")
	val categoryId: String? = null,

	@SerialName("price")
	val price: Float,

	@SerialName("estimated_prep")
	val estimatedPrep: String,

	@SerialName("is_available")
	val isAvailable: Boolean = false
)

// Fetch all MenuItem
suspend fun fetchBranchItem(): List<BranchItem> {
	return SupabaseClientProvider.supabase.postgrest["branch_items"].select().decodeList<BranchItem>()
}

suspend fun fetchBranchItemByBranch(branchId: String): List<BranchItem>? {
	val result = SupabaseClientProvider.supabase.postgrest["branch_items"]
		.select {
			filter {
				eq("branch_id", branchId)
			}
		}
		.decodeList<BranchItem>()
	return result
}
