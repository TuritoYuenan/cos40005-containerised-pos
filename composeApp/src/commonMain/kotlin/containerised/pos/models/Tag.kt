package containerised.pos.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
	@SerialName("tag_id")
	val tagId: String,

	@SerialName("tag_name")
	val tagName: String,

	@SerialName("tag_des")
	val tagDes: String? = null
)
