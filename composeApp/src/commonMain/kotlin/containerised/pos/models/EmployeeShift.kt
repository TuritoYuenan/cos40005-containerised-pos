package containerised.pos.models

import containerised.pos.database.SupabaseClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class EmployeeShift(
	@SerialName("user_id")
	val userId: String,

	@SerialName("start_time")
	val startTime: String,

	@SerialName("end_time")
	val endTime: String,

	@SerialName("date")
	val date: DayOfWeek
){
	companion object {
		suspend fun fetchById(userId: String): List<EmployeeShift>{
			return SupabaseClient.db["employee_shifts"]
				.select { filter { eq("user_id", userId) } }
				.decodeList<EmployeeShift>()
		}
		fun isInShift(hour: Int, shift: EmployeeShift): Boolean {
			val start = shift.startTime.take(2).toInt()
			val end = shift.endTime.take(2).toInt()
			return hour in start until end
		}
	}
}
