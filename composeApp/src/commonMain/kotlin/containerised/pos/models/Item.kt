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

// Add a new MenuItem
suspend fun addMenuItem(item: Item) {
	SupabaseClientProvider.supabase.postgrest["menu_items"].insert(item)
}

// Fetch all MenuItem
suspend fun fetchMenuItem(): List<Item> {
	return SupabaseClientProvider.supabase.postgrest["menu_items"].select().decodeList<Item>()
}
// Fetch a MenuItem by ID
suspend fun fetchMenuItemById(itemId: String): Item? {
	val result = SupabaseClientProvider.supabase.postgrest["menu_items"]
		.select {
			filter {
				eq("item_id", itemId)
			}
			limit(1)
		}
		.decodeList<Item>()
	return result.firstOrNull()
}
// Edit  MenuItem by ID
suspend fun updateMenuItem(itemId: String, updatedData: Item) {
	SupabaseClientProvider.supabase.postgrest["menu_items"]
		.update(updatedData) {
			filter {
				eq("item_id", itemId)
			}
		}
}

// Delete MenuItem by ID
suspend fun deleteMenuItem(itemId: String) {
	SupabaseClientProvider.supabase.postgrest["menu_items"]
		.delete {
			filter {
				eq("item_id", itemId)
			}
		}
}
