package containerised.pos.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Ingredient(
	@SerialName("ingredient_id")
	val ingredientId: String? = null,

	@SerialName("ingredient_name")
	val ingredientName: String? = null,

	@SerialName("unit")
	val unit: String? = null,

	@SerialName("current_stock")
	val currentStock: Double? = null,

	@SerialName("min_stock_level")
	val minStockLevel: Double? = null,

	@SerialName("supplier_info")
	val supplierInfo: JsonObject? = null,

	@SerialName("branch_id")
	val branchId: String? = null,

	@SerialName("is_active")
	val isActive: Boolean? = null
)

