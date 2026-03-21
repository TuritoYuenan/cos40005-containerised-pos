package containerised.pos.models

import containerised.pos.database.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Records a stock adjustment of an inventory item
 */
@Serializable
data class StockAdjustment(
	@SerialName("record_id")
	val id: String,

	@SerialName("ingredient_id")
	val ingredientID: String,

	var ingredient: Ingredient? = null, // Populated when fetching adjustments for an ingredient

	@SerialName("staff_id")
	val staffID: String,

	@SerialName("adjustment_type")
	val adjustmentType: Type,

	@SerialName("quantity_before")
	val quantityBefore: Double,

	@SerialName("quantity_after")
	val quantityAfter: Double,

	@SerialName("notes")
	val notes: String? = null,

	@SerialName("timestamp")
	val timestamp: String,
) {
	enum class Type { DIRECT, INCREMENT, DECREMENT }

	companion object {
		suspend fun fetchByIngredientWithDetails(id: String): List<StockAdjustment> {
			return SupabaseClient.db["stock_adjustments"]
				.select(Columns.raw("*, ingredient:ingredients(*)")) {
					filter { eq("ingredient_id", id) }
				}.decodeList<StockAdjustment>()
		}

		val MOCK = StockAdjustment(
			id = "mock-id",
			ingredientID = "mock-ingredient-id",
			ingredient = Ingredient.MOCK,
			staffID = "mock-staff-id",
			adjustmentType = Type.DIRECT,
			quantityBefore = 10.0,
			quantityAfter = 15.0,
			notes = "Added 5 units",
			timestamp = "2024-01-01T12:00:00Z"
		)
	}
}
