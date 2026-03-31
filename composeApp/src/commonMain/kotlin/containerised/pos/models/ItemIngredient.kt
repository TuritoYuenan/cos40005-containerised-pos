package containerised.pos.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemIngredient(
	@SerialName("ingredient_id")
	val ingredientId: String,

	var ingredient: Ingredient,

	@SerialName("item_id")
	val itemId: String,

	@SerialName("quantity")
	val quantity: Double? = null,

	@SerialName("unit")
	val unit: String? = null
)
