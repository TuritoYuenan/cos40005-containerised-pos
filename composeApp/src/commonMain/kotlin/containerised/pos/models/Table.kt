package containerised.pos.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Table(
	@SerialName("table_id")
	val tableId: String,

	@SerialName("branch_id")
	val branchId: String,

	@SerialName("table_code")
	val tableCode: String,

	@SerialName("is_active")
	val isActive: Boolean = true
)
