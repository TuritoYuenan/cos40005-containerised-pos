package containerised.pos.models

import containerised.pos.database.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.compose.resources.getString
import posapplication.composeapp.generated.resources.*

@Serializable
data class Ingredient(
	@SerialName("ingredient_id")
	val id: String,

	@SerialName("ingredient_name")
	val ingredientName: String,

	@SerialName("unit")
	val unit: String,

	@SerialName("current_stock")
	val currentStock: Double,

	@SerialName("min_stock_level")
	val minStockLevel: Double = 0.0,

	@SerialName("supplier_info")
	val supplierInfo: JsonObject = JsonObject(emptyMap()),

	@SerialName("branch_id")
	val branchId: String = "",

	@SerialName("is_active")
	val isActive: Boolean = true
) {
	@Serializable
	data class Insertable(
		@SerialName("ingredient_id")
		val id: String,

		@SerialName("ingredient_name")
		var name: String? = null,

		@SerialName("unit")
		var unit: String? = null,

		@SerialName("min_stock_level")
		var minStockLevel: Double? = null,

		@SerialName("current_stock")
		var currentStock: Double? = null,

		@SerialName("supplier_info")
		val supplierInfo: JsonObject? = null,

		@SerialName("branch_id")
		val branchId: String? = null
	) {
		fun hasChanged(original: Ingredient): Boolean {
			return name != original.ingredientName ||
				unit != original.unit ||
				minStockLevel != original.minStockLevel ||
				currentStock != original.currentStock
		}

		suspend fun getValidationErrors(): Map<String, String?> {
			val nameError = if (name.isNullOrBlank())
				getString(Res.string.ve_ingredient_name) else null

			val unitError = if (unit.isNullOrBlank())
				getString(Res.string.ve_unit) else null

			val minStockLevelError = when (minStockLevel) {
				null -> getString(Res.string.ve_min_stock)
				in Double.MIN_VALUE..-0.0000001 -> getString(Res.string.ve_neg_stock)
				else -> null
			}

			val currentStockError = when (currentStock) {
				null -> getString(Res.string.ve_stock)
				in Double.MIN_VALUE..-0.0000001 -> getString(Res.string.ve_neg_stock)
				else -> null
			}

			return mapOf(
				"ingredientName" to nameError,
				"minStockLevel" to minStockLevelError,
				"currentStock" to currentStockError,
				"unit" to unitError,
			)
		}

		suspend fun update(id: String) = SupabaseClient.db["ingredients"]
			.update(this) { filter { eq("ingredient_id", id) } }
	}

	fun toInsertable(): Insertable = Insertable(
		id = id,
		name = ingredientName,
		unit = unit,
		currentStock = currentStock,
		minStockLevel = minStockLevel,
		supplierInfo = supplierInfo,
		branchId = branchId
	)

	@Serializable
	data class DecreaseStockParams(
		@SerialName("id")
		val id: String,

		@SerialName("amount")
		val amount: Double,

		@SerialName("staff_id")
		val staffID: String,

		@SerialName("notes")
		val notes: String = "$staffID decreased stock by $amount units."
	)

	suspend fun decreaseStock(amount: Double, notes: String): Ingredient {
		val staffID = "37a7dccc-8a13-44ce-9981-efd5ed84d71c"

		SupabaseClient.db.rpc(
			"decrease_stock",
			DecreaseStockParams(id, amount, staffID, notes)
		)

		// Cannot trust old current stock due to race conditions
		return fetchByID(id)
			?: throw IllegalStateException("Failed to fetch updated ingredient")
	}

	fun isLowStock(): Boolean = currentStock < minStockLevel

	suspend fun markActive(isActive: Boolean?) = SupabaseClient.db["ingredients"]
		.update(mapOf("is_active" to isActive)) {
			filter { eq("ingredient_id", id) }
		}

	companion object {
		suspend fun fetchByBranch(
			branchId: String,
			mustBeActive: Boolean = true
		): List<Ingredient> = SupabaseClient.db["ingredients"]
			.select(Columns.ALL) {
				filter {
					eq("branch_id", branchId)
					if (mustBeActive) eq("is_active", true)
				}
			}.decodeList<Ingredient>()

		suspend fun fetchByID(id: String): Ingredient? = SupabaseClient.db["ingredients"]
			.select(Columns.ALL) { filter { eq("ingredient_id", id) } }
			.decodeSingleOrNull<Ingredient>()

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
