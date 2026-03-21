package containerised.pos.models

import containerised.pos.database.SupabaseClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Promotion(
	@SerialName("promotion_id")
	val promotionId: String,

	@SerialName("branch_id")
	val branchId: String,

	@SerialName("start_date")
	val startDate: String? = null,

	@SerialName("end_date")
	val endDate: String? = null,

	@SerialName("days_of_week")
	val daysOfWeek: String? = null,

	@SerialName("rules")
	val rules: String? = null, // JSONB comes as String

	@SerialName("is_active")
	val isActive: Boolean = true
) {
	companion object {
		suspend fun fetchById(promotionId: String): Promotion? {
			return SupabaseClient.db["promotions"]
				.select {
					filter { eq("promotion_id", promotionId) }
					limit(1)
				}
				.decodeList<Promotion>().firstOrNull()
		}

		suspend fun fetchByBranch(branchId: String): List<Promotion> {
			return SupabaseClient.db["promotions"]
				.select { filter { eq("branch_id", branchId) } }
				.decodeList()
		}

		suspend fun insert(
			startDate: String?,
			endDate: String?,
			daysOfWeek: String,
			rules: String,
			isActive: Boolean
		) = SupabaseClient.db["promotions"].insert(
			mapOf(
				"promotion_id" to "PRO",
				"branch_id" to "BRA26011700",
				"start_date" to startDate,
				"end_date" to endDate,
				"days_of_week" to daysOfWeek,
				"rules" to rules,
				"is_active" to isActive.toString() // 👈 important
			)
		)
	}
}
