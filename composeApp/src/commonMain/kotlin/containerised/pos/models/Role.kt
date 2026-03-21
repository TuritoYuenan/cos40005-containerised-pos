package containerised.pos.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Role(
	@SerialName("role_id")
	val roleId: String,

	@SerialName("role_name")
	val roleName: String,

	@SerialName("description")
	val description: String? = null,

	@SerialName("permission")
	val permission: List<String>
)
