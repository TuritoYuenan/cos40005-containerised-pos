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
    val rules: List<PromotionRule>? = null, // JSONB comes as String

	@SerialName("is_active")
	val isActive: Boolean
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

        suspend fun insert(promotion: PromotionInsert) =
            SupabaseClient.db["promotions"].insert(promotion)

        suspend fun updateById(id: String, data: PromotionInsert) {
            SupabaseClient.db["promotions"]
                .update(data) {
                    filter { eq("promotion_id", id) }
                }
        }

        suspend fun deleteById(id: String) {
            SupabaseClient.db["promotions"]
                .delete {
                    filter { eq("promotion_id", id) }
                }
        }
	}
}

@Serializable
data class DaySchedule(
    val day: String,
    val startTime: String,
    val endTime: String
)

@Serializable
data class PromotionRule(
    val rewardType: String = "FLAT_DISCOUNT",
    val targetType: String = "",
    val rewardValue: String = "",
    val rewardItems: Map<String, Int> = emptyMap(),
    val selectedIds: List<String> = emptyList(),
    val comboItems: Map<String, Int> = emptyMap()
)

@Serializable
data class PromotionInsert(
    val branch_id: String,
    val start_date: String? = null,
    val end_date: String? = null,
    val days_of_week: List<DaySchedule> = emptyList(),
    val rules: List<PromotionRule> = emptyList(),
    val is_active: Boolean
)