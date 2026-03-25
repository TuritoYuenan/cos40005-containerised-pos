package containerised.pos.models

import containerised.pos.database.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Represents a menu item available at a specific branch of a restaurant.
 */
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

	val itemIngredients: List<ItemIngredientWithIngredient>? = null,
	val itemTags: List<ItemTag>? = null
) {
	fun isOutOfStock(): Boolean {
		checkNotNull(itemIngredients) { "Must fetch ingredients to determine stock status." }
		return itemIngredients.any { it.ingredient.isLowStock() }
	}

	companion object {
		/**
		 * Fetches all branch items from the database.
		 * @return A list of [BranchItem] objects representing all branch items in the database.
		 * @throws Exception if there is an error during the database query or data decoding process.
		 * @see BranchItem
		 */
		suspend fun fetchAll(): List<BranchItem> = SupabaseClient.db["branch_items"]
			.select()
			.decodeList<BranchItem>()

		/**
		 * Fetches branch items associated with a specific branch ID from the database.
		 * @param branchId The ID of the branch for which to fetch items.
		 * @return A list of [BranchItem] objects representing the items associated with the specified branch ID.
		 * @throws Exception if there is an error during the database query or data decoding process.
		 * @see BranchItem
		 */
		suspend fun fetchByBranch(branchId: String): List<BranchItem> {
			return SupabaseClient.db["branch_items"]
				.select { filter { eq("branch_id", branchId) } }
				.decodeList<BranchItem>()
		}

		suspend fun fetchByBranchWithDetails(branchId: String): List<BranchItem> {
			val query = """
				*,
				itemIngredients: item_ingredients (*, ingredient: ingredients (*)),
				tags: item_tags (*, tag: tags (*)),
			""".trimIndent()

			return SupabaseClient.db["branch_items"]
				.select(Columns.raw(query)) {
					filter { eq("branch_id", branchId) }
				}.decodeList<BranchItem>()
		}

		suspend fun fetchById(id: String): BranchItem? = SupabaseClient.db["branch_items"]
			.select {
				filter { eq("item_id", id) }
				limit(1)
			}.decodeList<BranchItem>().firstOrNull()

		/**
		 * Updates a branch item in the database with the specified item ID using the provided updated data.
		 * @param itemId The ID of the item to update.
		 * @param updatedData A [BranchItem] object containing the updated data for the branch item.
		 * @throws Exception if there is an error during the database update process.
		 * @see BranchItem
		 */
		suspend fun update(itemId: String, updatedData: BranchItem) {
			SupabaseClient.db["branch_items"]
				.update(updatedData) { filter { eq("item_id", itemId) } }
		}

		val MOCK = BranchItem(
			branchId = "1",
			itemId = "1",
			categoryId = "1",
			category = Category(
				categoryId = "1",
				categoryName = "Main Course",
				displayOrder = 1
			),
			itemName = "Pho Bo",
			itemDes = "Vietnamese beef noodle soup",
			price = 50000,
			estimatedPrep = "15 mins",
			isAvailable = true,
			isFeatured = true,
			urlImg = null,
			itemIngredients = listOf(
				ItemIngredientWithIngredient(
					itemId = "0",
					ingredientId = "1",
					quantity = 1.1,
					ingredient = Ingredient(
						id = "1",
						ingredientName = "Beef",
						unit = "grams",
						currentStock = 10.0,
						minStockLevel = 15.0,
						supplierInfo = JsonObject(
							mapOf(
								"contact" to JsonPrimitive("0929340783"),
								"supplier" to JsonPrimitive("Supplier 1")
							)
						),
						branchId = "1",
						isActive = true
					)
				),
				ItemIngredientWithIngredient(
					itemId = "0",
					ingredientId = "2",
					quantity = 1.1,
					ingredient = Ingredient(
						id = "2",
						ingredientName = "Noodles",
						unit = "grams",
						currentStock = 10.0,
						minStockLevel = 5.0,
						supplierInfo = JsonObject(
							mapOf(
								"contact" to JsonPrimitive("0929340783"),
								"supplier" to JsonPrimitive("Supplier 2")
							)
						),
						branchId = "1",
						isActive = true
					)
				)
			)
		)
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
