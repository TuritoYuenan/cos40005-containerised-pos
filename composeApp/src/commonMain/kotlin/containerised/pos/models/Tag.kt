package containerised.pos.models

import containerised.pos.database.SupabaseClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
	}
}
