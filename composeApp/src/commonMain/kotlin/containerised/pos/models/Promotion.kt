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
	val daysOfWeek: List<DaySchedule>? = null,

	@SerialName("rules")
	val rules: List<Rule>? = null, // JSONB comes as String

	@SerialName("is_active")
	val isActive: Boolean,

	@SerialName("url_img")
	val urlImg: String? = null,
) {
	@Serializable
	data class Insertable(
		@SerialName("branch_id")
		val branchID: String,

		@SerialName("start_date")
		val startDate: String? = null,

		@SerialName("end_date")
		val endDate: String? = null,

		@SerialName("days_of_week")
		val daysOfWeek: List<DaySchedule> = emptyList(),

		@SerialName("rules")
		val rules: List<Rule> = emptyList(),

		@SerialName("is_active")
		val isActive: Boolean,

		@SerialName("url_img")
		val urlImg: String? = null,
	)

	@Serializable
	data class Rule(
		val rewardType: String = "FLAT_DISCOUNT",
		val targetType: String = "",
		val rewardValue: String = "",
		val rewardItems: Map<String, Int> = emptyMap(),
		val selectedIds: List<String> = emptyList(),
		val comboItems: Map<String, Int> = emptyMap()
	)

	companion object {
		suspend fun fetchById(id: String): Promotion? = SupabaseClient.db["promotions"]
			.select {
				filter { eq("promotion_id", id) }
				limit(1)
			}
			.decodeList<Promotion>().firstOrNull()

		suspend fun fetchByBranch(branchId: String): List<Promotion> {
			return SupabaseClient.db["promotions"]
				.select { filter { eq("branch_id", branchId) } }
				.decodeList()
		}

		suspend fun insert(promotion: Insertable) =
			SupabaseClient.db["promotions"].insert(promotion)

		suspend fun updateById(id: String, data: Insertable) =
			SupabaseClient.db["promotions"]
				.update(data) { filter { eq("promotion_id", id) } }

		suspend fun deleteById(id: String) = SupabaseClient.db["promotions"]
			.delete { filter { eq("promotion_id", id) } }
	}
}

@Serializable
data class DaySchedule(
	val day: String,
	val startTime: String,
	val endTime: String
)
