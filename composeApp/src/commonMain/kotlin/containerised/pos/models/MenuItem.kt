package containerised.pos.models

import containerised.pos.database.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuItem(
	@SerialName("item_id")
	val id: String,

	@SerialName("item_name")
	val name: String,

	@SerialName("item_des")
	val description: String?,

	@SerialName("price")
	val price: Float,

	@SerialName("category_id")
	val categoryID: String,

	@SerialName("is_available")
	val isAvailable: Boolean?,

	@SerialName("est_prep_time")
	val estimatedPreparationTime: Int?,

	@SerialName("img_url")
	val imageURL: String?,

	@SerialName("branch_id")
	val branchID: String,

	@SerialName("special_notes")
	val specialNotes: String?
)

// Add a new MenuItem
suspend fun addMenuItem(item: MenuItem) {
	SupabaseClientProvider.supabase.postgrest["menu_items"].insert(item)
}

// Fetch all MenuItem
suspend fun fetchMenuItem(): List<MenuItem> {
	return SupabaseClientProvider.supabase.postgrest["menu_items"].select().decodeList<MenuItem>()
}
// Fetch a MenuItem by ID
suspend fun fetchMenuItemById(itemId: String): MenuItem? {
	val result = SupabaseClientProvider.supabase.postgrest["menu_items"]
		.select {
			filter {
				eq("item_id", itemId)
			}
			limit(1)
		}
		.decodeList<MenuItem>()
	return result.firstOrNull()
}
// Edit  MenuItem by ID
suspend fun updateMenuItem(itemId: String, updatedData: MenuItem) {
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
