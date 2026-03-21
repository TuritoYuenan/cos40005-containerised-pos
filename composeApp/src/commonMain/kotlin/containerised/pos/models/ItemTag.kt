package containerised.pos.models

import containerised.pos.database.SupabaseClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemTag(
	@SerialName("item_id")
	val itemId: String,

	@SerialName("tag_id")
	val tagId: String
) {
	companion object {
		suspend fun fetchByItemId(itemId: String): List<ItemTag> {
			return SupabaseClient.db["item_tags"]
				.select { filter { eq("item_id", itemId) } }
				.decodeList<ItemTag>()
		}
	}
}
