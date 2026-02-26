package containerised.pos.models

import containerised.pos.database.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BranchItem(
	@SerialName("branch_id")
	val branchId: String,

	@SerialName("item_id")
	val itemId: String,

	@SerialName("category_id")
	val categoryId: String? = null,

	val category: Category? = null,

	@SerialName("item_name")
	val itemName: String,

	@SerialName("item_des")
	val itemDes: String? = null,

	@SerialName("price")
	val price: Int,

	@SerialName("estimated_prep")
	val estimatedPrep: String,

	@SerialName("is_available")
	val isAvailable: Boolean = false,

	@SerialName("is_featured")
	val isFeatured: Boolean = false,

	@SerialName("url_img")
	val urlImg: String? = null,
) {
	companion object {
		/**
		 * Fetches all branch items from the database.
		 * @return A list of [BranchItem] objects representing all branch items in the database.
		 * @throws Exception if there is an error during the database query or data decoding process.
		 * @see BranchItem
		 */
		suspend fun fetchAll(): List<BranchItem> {
			return SupabaseClientProvider.supabase.postgrest["branch_items"].select().decodeList<BranchItem>()
		}

		/**
		 * Fetches branch items associated with a specific branch ID from the database.
		 * @param branchId The ID of the branch for which to fetch items.
		 * @return A list of [BranchItem] objects representing the items associated with the specified branch ID.
		 * @throws Exception if there is an error during the database query or data decoding process.
		 * @see BranchItem
		 */
		suspend fun fetchByBranch(branchId: String): List<BranchItem> {
			val result = SupabaseClientProvider.supabase.postgrest["branch_items"]
				.select {
					filter {
						eq("branch_id", branchId)
					}
				}
				.decodeList<BranchItem>()
			return result
		}

		/**
		 * Fetches a branch item by its item ID from the database.
		 * @param itemId The ID of the item to fetch.
		 * @return A [BranchItem] object representing the branch item with the specified item ID, or null if no such item exists.
		 * @throws Exception if there is an error during the database query or data decoding process.
		 * @see BranchItem
		 */
		suspend fun fetchById(itemId: String): BranchItem? {
			val result = SupabaseClientProvider.supabase.postgrest["branch_items"]
				.select {
					filter {
						eq("item_id", itemId)
					}
					limit(1)
				}
				.decodeList<BranchItem>()
			return result.firstOrNull()
		}

		/**
		 * Fetches branch items associated with a specific branch ID from the database, including related category information.
		 * @param branchId The ID of the branch for which to fetch items.
		 * @return A list of [BranchItem] objects representing the items associated with the specified branch ID, including related category information.
		 * @throws Exception if there is an error during the database query or data decoding process.
		 * @see BranchItem
		 */
		suspend fun fetchAndJoinByBranch(branchId: String): List<BranchItem> {
			val result = SupabaseClientProvider.supabase.postgrest["branch_items"]
				.select(
					columns = Columns.raw(
						"""
						*,
						category: categories (category_id, category_name, display_order)
						"""
					)
				) {
					filter {
						eq("branch_id", branchId)
					}
				}.decodeList<BranchItem>()

			return result
		}

		/**
		 * Fetches a branch item by its item ID from the database, including related category information.
		 * @param itemId The ID of the item to fetch.
		 * @return A [BranchItem] object representing the branch item with the specified item ID, including related category information, or null if no such item exists.
		 * @throws Exception if there is an error during the database query or data decoding process.
		 * @see BranchItem
		 */
		suspend fun fetchAndJoinById(itemId: String): BranchItem? {
			val result = SupabaseClientProvider.supabase.postgrest["branch_items"]
				.select(
					columns = Columns.raw(
						"""
						*,
						category: categories (category_id, category_name, display_order)
						"""
					)
				) {
					filter {
						eq("item_id", itemId)
					}
					limit(1)
				}.decodeList<BranchItem>()

			return result.firstOrNull()
		}

		/**
		 * Updates a branch item in the database with the specified item ID using the provided updated data.
		 * @param itemId The ID of the item to update.
		 * @param updatedData A [BranchItem] object containing the updated data for the branch item.
		 * @throws Exception if there is an error during the database update process.
		 * @see BranchItem
		 */
		suspend fun update(itemId: String, updatedData: BranchItem) {
			SupabaseClientProvider.supabase.postgrest["branch_items"]
				.update(updatedData) {
					filter {
						eq("item_id", itemId)
					}
				}
		}
	}
}
@Serializable
data class BranchItemWithCatAndIng(
	@SerialName("branch_id")
	val branchId: String,

	@SerialName("item_id")
	val itemId: String,

	@SerialName("category_id")
	val categoryId: String? = null,

	val category: Category,

	val itemIngredients: List<ItemIngredientWithIngredient>,

	@SerialName("item_name")
	val itemName: String,

	@SerialName("item_des")
	val itemDes: String? = null,

	@SerialName("price")
	val price: Int,

	@SerialName("estimated_prep")
	val estimatedPrep: String,

	@SerialName("is_available")
	val isAvailable: Boolean = false,

	@SerialName("is_featured")
	val isFeatured: Boolean = false,

	@SerialName("url_img")
	val urlImg: String? = null,
)
