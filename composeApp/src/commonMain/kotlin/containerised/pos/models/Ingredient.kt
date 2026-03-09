package containerised.pos.models

import containerised.pos.database.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class Ingredient(
	@SerialName("ingredient_id")
	val id: String? = null,

	@SerialName("ingredient_name")
	var ingredientName: String? = null,

	@SerialName("unit")
	var unit: String? = null,

	@SerialName("current_stock")
	var currentStock: Double? = null,

	@SerialName("min_stock_level")
	var minStockLevel: Double? = null,

	@SerialName("supplier_info")
	val supplierInfo: JsonObject? = null,

	@SerialName("branch_id")
	val branchId: String? = null,

	@SerialName("is_active")
	val isActive: Boolean? = null
) {
	suspend fun update() {
		if (id == null) throw IllegalStateException("Ingredient ID is required for update")
		SupabaseClient.db["ingredients"]
			.update(this) {
				filter { eq("ingredient_id", id) }
			}
	}

	suspend fun markActive(isActive: Boolean?) {
		if (id == null) throw IllegalStateException("Ingredient ID is required to mark inactive")
		SupabaseClient.db["ingredients"]
			.update(mapOf("is_active" to isActive)) {
				filter { eq("ingredient_id", id) }
			}
	}

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

		suspend fun fetchByID(ingredientId: String): Ingredient? {
			return SupabaseClient.db["ingredients"]
				.select(Columns.ALL) {
					filter { eq("ingredient_id", ingredientId) }
				}.decodeSingleOrNull<Ingredient>()
		}

		@Serializable
		data class DecreaseStockRequest(
			val id: String,
			val amount: Double
		)

		suspend fun decreaseStock(ingredientId: String, amount: Double) {
			SupabaseClient.db
				.rpc(
					"decrease_stock",
					DecreaseStockRequest(
						id = ingredientId,
						amount = amount
					)
				)
		}

		val MOCK = Ingredient(
			branchId = "BRA26011700",
			id = "1",
			ingredientName = "Tomato",
			isActive = true,
			currentStock = 50.0,
			minStockLevel = 10.0,
			unit = "grams",
			supplierInfo = JsonObject(
				mapOf(
					"contact" to JsonPrimitive("0929340783"),
					"supplier" to JsonPrimitive("Supplier 2")
				)
			)
		)
	}
}
