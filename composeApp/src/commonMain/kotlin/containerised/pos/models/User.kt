package containerised.pos.models

import containerised.pos.database.SupabaseClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
enum class DayOfWeek {
	MON, TUE, WED, THU, FRI, SAT, SUN
}
val days = DayOfWeek.entries
val hours = (6..21).map { hour ->
	"${hour.toString().padStart(2, '0')}:00"
}
/**
 * Represents a user in the POS system.
 */
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
	val isActive: Boolean = false,

	@SerialName("created_at")
	val createdAt: String,

	@SerialName("updated_at")
	val updatedAt: String? = null,

	@SerialName("employee_status")
	val employeeStatus: EmployeeStatus? = null,

	@SerialName("shift")
	val shift: Map<DayOfWeek, List<String>> = emptyMap()
) {
	@Serializable
	enum class EmployeeStatus { ACTIVE, INACTIVE, BREAK, OFF_DUTY }
	companion object {
		suspend fun fetchById(userId: String): User? = SupabaseClient.db["users"]
			.select { filter { eq("user_id", userId) } }
			.decodeList<User>().firstOrNull()

		suspend fun fetchByBranch(branchId: String): List<User> = SupabaseClient.db["users"]
			.select { filter { eq("branch_id", branchId) } }
			.decodeList<User>()
	}
}
