package containerised.pos.models

import containerised.pos.database.SupabaseClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemIngredient(
	@SerialName("ingredient_id")
	val ingredientId: String,

	@SerialName("item_id")
	val itemId: String,

	@SerialName("quantity")
	val quantity: Double? = null,

	@SerialName("unit")
	val unit: String? = null
) {
	companion object {
		suspend fun fetchItemIngredientByItemId(itemId: String): List<ItemIngredient> {
			val result = SupabaseClient.db["item_ingredients"]
				.select {
					filter {
						eq("item_id", itemId)
					}
				}
				.decodeList<ItemIngredient>()
			return result
		}
	}
}

@Serializable
data class ItemIngredientWithIngredient(
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
