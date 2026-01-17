package containerised.pos.models

import kotlinx.datetime.LocalDateTime
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
)
