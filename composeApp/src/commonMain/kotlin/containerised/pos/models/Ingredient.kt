package containerised.pos.models

import containerised.pos.database.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import containerised.pos.database.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Ingredient(
	@SerialName("ingredient_id")
	val id: String? = null,

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
) {
	companion object {
		suspend fun fetchByBranch(branchId: String, mustBeActive: Boolean = true): List<Ingredient> {
			return SupabaseClient.db["ingredients"]
				.select(Columns.ALL) {
					filter {
						eq("branch_id", branchId)
						if (mustBeActive) eq("is_active", true)
					}
				}.decodeList<Ingredient>()
		}
	}
}
		@Serializable
		data class DecreaseStockRequest(
			val id: String,

			val amount: Double
		)

		suspend fun decreaseStock(ingredientId: String, amount: Double) {
			SupabaseClientProvider.supabase.postgrest
				.rpc(
					"decrease_stock",
					DecreaseStockRequest(
						id = ingredientId,
						amount = amount
					)
				)
		}
	}
}

