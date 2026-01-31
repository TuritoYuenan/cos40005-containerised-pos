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

	@SerialName("category_id")
	val categoryId: String? = null,

    val category: Category? = null,

	@SerialName("item_name")
	val itemName: String,

	@SerialName("item_des")
	val itemDes: String? = null,

	@SerialName("price")
	val price: Float,

	@SerialName("estimated_prep")
	val estimatedPrep: String,

	@SerialName("is_available")
	val isAvailable: Boolean = false,

	@SerialName("is_featured")
	val isFeatured: Boolean = false,

	@SerialName("url_img")
	val urlImg: String? = null,
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

suspend fun fetchBranchItemById(itemId: String): BranchItem? {
	val result = SupabaseClientProvider.supabase.postgrest["branch_items"]
		.select {
			filter {
				eq("item_id", itemId)
			}
			limit(1)
		}
		.decodeList<BranchItem>()
	return result.firstOrNull()
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
suspend fun fetchAndJoinBranchItemById(itemId: String): BranchItem? {
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
				eq("item_id", itemId)
			}
			limit(1)
		}.decodeList<BranchItem>()

	return result.firstOrNull()
}

suspend fun updateBranchItem(itemId: String, updatedData: BranchItem) {
	SupabaseClientProvider.supabase.postgrest["branch_items"]
		.update(updatedData) {
			filter {
				eq("item_id", itemId)
			}
		}
}
