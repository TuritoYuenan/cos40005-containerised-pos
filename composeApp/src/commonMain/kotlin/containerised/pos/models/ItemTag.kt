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
        suspend fun insertTags(itemId: String, tagIds: Set<String>) {
            val rows = tagIds.map {
                ItemTag(
                    itemId = itemId,
                    tagId = it
                )
            }
            SupabaseClient.db["item_tags"].insert(rows)
        }
        suspend fun updateTags(
            itemId: String,
            oldTagIds: Set<String>,
            newTagIds: Set<String>
        ) {
            if (oldTagIds == newTagIds) return
            val table = SupabaseClient.db["item_tags"]
            val toDelete = oldTagIds - newTagIds
            val toInsert = newTagIds - oldTagIds
            if (toDelete.isNotEmpty()) {
                table.delete {
                    filter {
                        eq("item_id", itemId)
                        isIn("tag_id", toDelete.toList())
                    }
                }
            }
            if (toInsert.isNotEmpty()) {
                val rows = toInsert.map {
                    ItemTag(
                        itemId = itemId,
                        tagId = it
                    )
                }
                table.insert(rows)
            }
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
