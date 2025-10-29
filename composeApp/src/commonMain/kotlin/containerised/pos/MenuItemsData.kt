package containerised.pos

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class MenuItem(
	val itemId: String,
	val itemName: String,
	val itemDesc: String,
	val price: Float,
	val categoryId: String,
	val isAvailable: Boolean,
	val estPrepTime: Int,
	val imgUrl: String?,
	val branchId: String,
	val specialNotes: String?
)

// Add a new MenuItem
suspend fun addMenuItem(menuitem: MenuItem) {
	SupabaseClientProvider.supabase.postgrest["menu_items"].insert(menuitem)
}

// Fetch all MenuItem
suspend fun fetchMenuItem(): List<MenuItem> {
	return SupabaseClientProvider.supabase.postgrest["menu_items"].select().decodeList<MenuItem>()
}

