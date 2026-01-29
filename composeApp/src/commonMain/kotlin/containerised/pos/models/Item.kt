package containerised.pos.models

import containerised.pos.database.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
	@SerialName("item_id")
	val itemId: String,

	@SerialName("item_name")
	val itemName: String,

	@SerialName("item_des")
	val itemDes: String? = null,

	@SerialName("default_price")
	val defaultPrice: Float,

	@SerialName("default_estimated_prep")
	val defaultEstimatedPrep: String
)

/**
 * Add a new Item
 */
suspend fun addItem(item: Item) {
	SupabaseClientProvider.supabase.postgrest["items"].insert(item)
}

/**
 * Fetch all Items
 */
suspend fun fetchItem(): List<Item> {
	return SupabaseClientProvider.supabase.postgrest["items"].select().decodeList<Item>()
}

/**
 * Fetch Item by ID
 */
suspend fun fetchItemById(itemId: String): Item? {
	val result = SupabaseClientProvider.supabase.postgrest["items"]
		.select {
			filter {
				eq("item_id", itemId)
			}
			limit(1)
		}
		.decodeList<Item>()
	return result.firstOrNull()
}

/**
 * Fetch Items by Branch ID
 */
suspend fun fetchItemByBranch(branchId: String): List<Item>{
	val branchItem = fetchBranchItemByBranch(branchId)
	val item = branchItem.mapNotNull { branchItem -> fetchItemById(branchItem.itemId) }
	return item
}

/**
 * Update Item by ID
 */
suspend fun updateItem(itemId: String, updatedData: Item) {
	SupabaseClientProvider.supabase.postgrest["items"]
		.update(updatedData) { filter { eq("item_id", itemId) } }
}

/**
 * Delete Item by ID
 */
suspend fun deleteItem(itemId: String) {
	SupabaseClientProvider.supabase.postgrest["items"]
		.delete { filter { eq("item_id", itemId) } }
}
