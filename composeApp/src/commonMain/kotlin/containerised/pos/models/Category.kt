package containerised.pos.models

import containerised.pos.database.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category(
	@SerialName("category_id")
	val categoryId: String,

	@SerialName("category_name")
	val categoryName: String,

	@SerialName("display_order")
	val displayOrder: Int = 1
) {
	companion object {
		suspend fun fetchAll(): List<Category> {
			return SupabaseClientProvider.supabase.postgrest["categories"].select().decodeList<Category>()
		}

		suspend fun fetchById(categoryId: String): Category? {
			return SupabaseClientProvider.supabase.postgrest["categories"]
				.select {
					filter {
						eq("category_id", categoryId)
					}
					limit(1)
				}
				.decodeList<Category>()
				.firstOrNull()
		}
	}
}
