package containerised.pos.models

import containerised.pos.database.SupabaseClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Promotion(
	@SerialName("promotion_id")
	val promotionId: String,

	@SerialName("name")
	val name: String,

	@SerialName("description")
	val description: String? = null,

	@SerialName("is_active")
	val isActive: Boolean = true,

	@SerialName("start_date")
	val startDate: String? = null,

	@SerialName("end_date")
	val endDate: String? = null,

	@SerialName("requires_manual_confirmation")
	val requiresManualConfirmation: Boolean = false,

	@SerialName("notes")
	val notes: String? = null
){
    companion object {
        suspend fun fetchById(promotionId: String): Promotion? {
            val result = SupabaseClient.db["promotions"]
                .select {
                    filter {
                        eq("promotion_id", promotionId)
                    }
                    limit(1)
                }
                .decodeList<Promotion>()

            return result.firstOrNull()
        }

        suspend fun fetchByBranch(branchId: String): List<Promotion> {
            val result = SupabaseClient.db["promotions"]
                .select {
                    filter {
                        eq("branch_id", branchId)
                    }
                }
                .decodeList<Promotion>()
            return result
        }
    }
}
