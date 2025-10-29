package containerised.pos

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class MenuItem(
	val item_id: String,
	val item_name: String,
	val item_des: String?,
	val price: Float,
	val category_id: String,
	val is_available: Boolean,
	val est_prep_time: Int?,
	val img_url: String?,
	val branch_id: String,
	val special_notes: String?
)

// Add a new MenuItem
suspend fun addMenuItem(menuitem: MenuItem) {
	SupabaseClientProvider.supabase.postgrest["menu_items"].insert(menuitem)
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
