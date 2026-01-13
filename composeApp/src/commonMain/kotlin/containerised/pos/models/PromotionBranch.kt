package containerised.pos.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromotionBranch(
	@SerialName("promotion_id")
	val promotionId: String,

	@SerialName("branch_id")
	val branchId: String,

	@SerialName("is_active")
	val isActive: Boolean = true,

	@SerialName("url_img")
	val urlImg: String
)
