package containerised.pos.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BranchItem(
	@SerialName("branch_id")
	val branchId: String,

	@SerialName("item_id")
	val itemId: String,

	@SerialName("category_id")
	val categoryId: String? = null,

	@SerialName("price")
	val price: Float,

	@SerialName("estimated_prep")
	val estimatedPrep: String,

	@SerialName("is_available")
	val isAvailable: Boolean = false
)
