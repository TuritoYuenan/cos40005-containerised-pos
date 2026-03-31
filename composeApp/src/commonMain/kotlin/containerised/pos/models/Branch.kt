package containerised.pos.models

import containerised.pos.database.SupabaseClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Branch(
	@SerialName("branch_id")
	val branchId: String,

	@SerialName("branch_name")
	val branchName: String,

	@SerialName("address")
	val address: String? = null,

	@SerialName("phone_number")
	val phoneNumber: String? = null,

	@SerialName("email")
	val email: String? = null,

	@SerialName("is_active")
	val isActive: Boolean = true,

	@SerialName("created_at")
	val createdAt: String? = null
) {
	companion object {
		suspend fun add(branch: Branch) {
			SupabaseClient.db["branches"].insert(branch)
		}

		suspend fun fetchAll(): List<Branch> {
			return SupabaseClient.db["branches"].select().decodeList<Branch>()
		}

		val MOCK = Branch(
			branchId = "branch123",
			branchName = "Main Branch",
			address = "123 Main St",
			phoneNumber = "555-1234",
			email = "branch@business.com",
			isActive = true,
			createdAt = "2024-01-01T00:00:00Z",
		)
	}
}
