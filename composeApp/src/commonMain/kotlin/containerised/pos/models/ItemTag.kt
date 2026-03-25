package containerised.pos.models

import containerised.pos.database.SupabaseClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemTag(
	@SerialName("item_id")
	val itemId: String,

	@SerialName("tag_id")
	val tagId: String,

	val tag: Tag? = null
) {
	companion object {
		suspend fun fetchByItemId(itemId: String): List<ItemTag> {
			return SupabaseClient.db["item_tags"]
				.select { filter { eq("item_id", itemId) } }
				.decodeList<ItemTag>()
		}

		val MOCK = ItemTag(
			itemId = "item123",
			tagId = "tag456",
			tag = Tag(
				tagId = "tag456",
				tagName = "Bestseller",
				tagDes = "Top-selling item"
			)
		)
	}
}
