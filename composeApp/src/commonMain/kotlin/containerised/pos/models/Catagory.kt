package containerised.pos.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category(
	@SerialName("category_id")
	val categoryId: String,

	@SerialName("category_name")
	val categoryName: String,

	@SerialName("display_order")
	val displayOrder: Int = 1
)
