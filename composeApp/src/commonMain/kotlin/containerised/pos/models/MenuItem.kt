package containerised.pos.models

import containerised.pos.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class MenuItem(
	val item_id: String,
	val item_name: String,
	val item_des: String?,
	val price: Float,
	val category_id: String,
	val is_available: Boolean?,
	val est_prep_time: Int?,
	val img_url: String?,
	val branch_id: String,
	val special_notes: String?
) {
	companion object {
		suspend fun fetch(): List<MenuItem> {
			return SupabaseClientProvider
				.supabase
				.postgrest["menu_items"]
				.select()
				.decodeList<MenuItem>()
		}
	}
}
