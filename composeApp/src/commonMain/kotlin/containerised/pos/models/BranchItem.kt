package containerised.pos.models

import containerised.pos.database.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Column
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BranchItem(
	@SerialName("branch_id")
	val branchId: String,

	@SerialName("item_id")
	val itemId: String,

    val item: Item? = null,

	@SerialName("category_id")
	val categoryId: String? = null,

    val category: Category? = null,

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

suspend fun fetchBranchItemByBranch(branchId: String): List<BranchItem> {
    val result = SupabaseClientProvider.supabase.postgrest["branch_items"]
        .select {
            filter {
                eq("branch_id", branchId)
            }
        }
        .decodeList<BranchItem>()
    return result
}

suspend fun fetchAndJoinBranchItemByBranch(branchId: String): List<BranchItem> {
    val result = SupabaseClientProvider.supabase.postgrest["branch_items"]
        .select(
            columns = Columns.raw(
                """
                branch_id,
                item_id,
                category_id,
                price,
                estimated_prep,
                is_available,
                item:items (
                    item_id,
                    item_name,
                    item_desc,
                    default_price,
                    default_estimated_prep
                ),
                category:categories (
                    category_id,
                    category_name,
                    display_order
                )
                """
            )
        ) {
            filter {
                eq("branch_id", branchId)
            }
        }.decodeList<BranchItem>()

    return result
}
