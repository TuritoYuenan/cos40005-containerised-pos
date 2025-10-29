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

