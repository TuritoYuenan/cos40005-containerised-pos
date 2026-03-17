package containerised.pos.models

import containerised.pos.database.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class UserRole(

	@SerialName("user_id")
	val userId: String,

	@SerialName("role_id")
	val roleId: String,

	val role: Role
){
	companion object {
		suspend fun fetchUserPermission(userId: String): List<String> {

			val result = SupabaseClient.db
				.from("user_roles")
				.select(
					Columns.raw("*, role:roles(*)")
				) {
					filter {
						eq("user_id", userId)
					}
				}
				.decodeList<UserRole>()

			return result.map { it.role.permission }
		}
	}
}
