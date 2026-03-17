package containerised.pos.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(

	@SerialName("user_id")
	val userId: String,

	@SerialName("email")
	val email: String,

	@SerialName("full_name")
	val fullName: String,

	@SerialName("phone")
	val phone: String? = null,

	@SerialName("branch_id")
	val branchId: String,

	@SerialName("is_active")
	val isActive: Boolean? = null,

	@SerialName("created_at")
	val createdAt: String,

	@SerialName("updated_at")
	val updatedAt: String? = null
)
