package containerised.pos.models

import containerised.pos.database.SupabaseClient
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
enum class DayOfWeek {
	MON, TUE, WED, THU, FRI, SAT, SUN
}
val days = DayOfWeek.entries
val hours = (6..22).map { hour ->
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
	val shift: Map<DayOfWeek, List<String>> = emptyMap(),

	@SerialName("last_login_time")
	val lastLoginTime: String? = null,

	@SerialName("last_logout_time")
	val lastLogoutTime: String? = null,
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

		fun mapDay(day: kotlinx.datetime.DayOfWeek): DayOfWeek {
			return when (day) {
				kotlinx.datetime.DayOfWeek.MONDAY -> DayOfWeek.MON
				kotlinx.datetime.DayOfWeek.TUESDAY -> DayOfWeek.TUE
				kotlinx.datetime.DayOfWeek.WEDNESDAY -> DayOfWeek.WED
				kotlinx.datetime.DayOfWeek.THURSDAY -> DayOfWeek.THU
				kotlinx.datetime.DayOfWeek.FRIDAY -> DayOfWeek.FRI
				kotlinx.datetime.DayOfWeek.SATURDAY -> DayOfWeek.SAT
				kotlinx.datetime.DayOfWeek.SUNDAY -> DayOfWeek.SUN
			}
		}
		//Off duty if now is not in shift, inactive if now is in shift
		fun getStatusFromShift(
			shift: Map<DayOfWeek, List<String>>,
			now: LocalDateTime = Clock.System.now()
				.toLocalDateTime(TimeZone.currentSystemDefault())
		): EmployeeStatus {

			val today = mapDay(now.date.dayOfWeek)
			val todayShifts = shift[today] ?: return EmployeeStatus.OFF_DUTY

			if (todayShifts.isEmpty()) return EmployeeStatus.OFF_DUTY

			val currentTime = now.time

			for (slot in todayShifts) {
				val slotTime = LocalTime.parse(slot)

				// If current time is within this hour slot
				if (currentTime.hour == slotTime.hour) {
					return EmployeeStatus.INACTIVE
				}
			}

			return EmployeeStatus.OFF_DUTY
		}
		fun getCurrentTimestamp(): String {
			return Clock.System.now().toString()
		}
		suspend fun updateLastLogin(userId: String) {
			val now = getCurrentTimestamp()

			SupabaseClient.db
				.from("users")
				.update(
					mapOf("last_login_time" to now)
				) {
					filter {
						eq("user_id", userId)
					}
				}
		}
		suspend fun updateLastLogout(userId: String) {
			val now = getCurrentTimestamp()

			SupabaseClient.db
				.from("users")
				.update(
					mapOf("last_logout_time" to now)
				) {
					filter {
						eq("user_id", userId)
					}
				}
		}
	}
}
