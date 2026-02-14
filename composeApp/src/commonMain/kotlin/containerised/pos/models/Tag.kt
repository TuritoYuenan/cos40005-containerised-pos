package containerised.pos.models

import containerised.pos.database.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
	@SerialName("tag_id")
	val tagId: String,

	@SerialName("tag_name")
	val tagName: String,

	@SerialName("tag_desc")
	val tagDes: String? = null
) {
	companion object {
		suspend fun fetchAll(): List<Tag> {
			return SupabaseClientProvider.supabase.postgrest["tags"].select().decodeList<Tag>()
		}
	}
}
