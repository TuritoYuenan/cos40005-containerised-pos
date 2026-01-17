package containerised.pos.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class PromotionCondition(
	@SerialName("condition_id")
	val conditionId: String,

	@SerialName("promotion_id")
	val promotionId: String,

	@SerialName("condition_type")
	val conditionType: String,

	@SerialName("value")
	val value: JsonElement
)
