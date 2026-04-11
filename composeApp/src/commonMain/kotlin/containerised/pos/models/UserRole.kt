package containerised.pos.models

import containerised.pos.database.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRole(

	@SerialName("user_id")
	val userId: String,

	val user: User? = null,

	@SerialName("role_id")
	val roleId: String,

	val role: Role
) {
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

			return result.flatMap { it.role.permission }
		}
		suspend fun fetchAndJoin(userId: String): UserRole? {

			val result = SupabaseClient.db
				.from("user_roles")
				.select(
					Columns.raw("*, user:users(*), role:roles(*)")
				) {
					filter {
						eq("user_id", userId)
					}
				}
				.decodeList<UserRole>()

			return result.firstOrNull()
		}
		suspend fun fetchAndJoinByBranch(branchId: String): List<UserRole> {

			return SupabaseClient.db
				.from("user_roles")
				.select(
					Columns.raw("*, user:users(*), role:roles(*)")
				) {
					filter {
						eq("user.branch_id", branchId)
					}
				}
				.decodeList<UserRole>()
		}
	}
}
