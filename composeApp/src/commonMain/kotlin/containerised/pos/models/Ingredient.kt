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
	@Serializable
	data class DecreaseStockParams(
		@SerialName("id")
		val id: String,

		@SerialName("amount")
		val amount: Double,

		@SerialName("staff_id")
		val staffID: String,

		@SerialName("notes")
		val notes: String? = null
	)

	suspend fun decreaseStock(amount: Double, notes: String? = null): Ingredient {
		if (id == null) throw IllegalStateException("Cannot identify ingredient without ID")
		val staffID = "37a7dccc-8a13-44ce-9981-efd5ed84d71c"

		SupabaseClient.db.rpc(
			"decrease_stock",
			DecreaseStockParams(id, amount, staffID, notes)
		)

		// Cannot trust old current stock due to race conditions
		return fetchByID(id) ?: throw IllegalStateException("Failed to fetch updated ingredient")
	}

	fun isLowStock(): Boolean {
		val current = currentStock ?: return false
		val min = minStockLevel ?: return false
		return current < min
	}

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
