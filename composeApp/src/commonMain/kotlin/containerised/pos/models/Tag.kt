package containerised.pos.models

import containerised.pos.database.SupabaseClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a tag used for categorizing menu items.
 * For example, a dish can be "vegan" (tag), "spicy" (tag), etc.
 */
@Serializable
data class Tag(
	@SerialName("tag_id")
	val tagId: String,

	@SerialName("tag_name")
	val tagName: String,

	@SerialName("tag_desc")
	val tagDes: String? = null
) {
	companion object {
		suspend fun fetchAll(): List<Tag> {
			return SupabaseClient.db["tags"].select().decodeList<Tag>()
		}

		val MOCKS = listOf(
			Tag("1", "Beverages", "Drinks and refreshments"),
			Tag("2", "Snacks", "Light bites and appetizers"),
			Tag("3", "Desserts", "Sweet treats to end your meal"),
			Tag("4", "Vegan", "Plant-based options for everyone"),
			Tag("5", "Gluten-Free", "Delicious dishes without gluten"),
		)
	}
}
