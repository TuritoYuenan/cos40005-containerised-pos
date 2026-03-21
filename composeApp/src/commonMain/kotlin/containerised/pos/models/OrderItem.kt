package containerised.pos.models

import containerised.pos.database.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
	@SerialName("order_id")
	val orderId: String,

	@SerialName("item_id")
	val itemId: String,

	val branchItem: BranchItemWithCatAndIng? = null,

	@SerialName("quantity")
	val quantity: Int,

	@SerialName("subtotal")
	val subtotal: Int? = null,

	@SerialName("special_notes")
	val specialNotes: String? = null,

	@SerialName("item_status")
	val itemStatus: OrderStatus? = null
) {
	@Serializable
	data class Insertable(
		@SerialName("order_id")
		val orderId: String,

		@SerialName("item_id")
		val itemId: String,

		@SerialName("quantity")
		val quantity: Int,

		@SerialName("subtotal")
		val subtotal: Int? = null,

		@SerialName("special_notes")
		val specialNotes: String? = null,

		@SerialName("item_status")
		val itemStatus: OrderStatus? = null
	) {
		suspend fun add() = SupabaseClient.db["order_items"].insert(this)
	}

	companion object {
		suspend fun fetchByOrderWithJoins(orderId: String): List<OrderItem> {
			val query = """
				order_id,
				item_id,
				branchItem:branch_items (
					item_id,
					branch_id,
					category_id,
					category:categories (category_id, category_name),
					itemIngredients:item_ingredients(
						ingredient_id,
						ingredient: ingredients(ingredient_id, ingredient_name, unit, current_stock),
						item_id,
						quantity,
						unit
					),
					item_name,
					item_desc,
					price,
					estimated_prep
				),
				quantity,
				subtotal,
				special_notes,
				item_status
			""".trimIndent()

			return SupabaseClient.db["order_items"]
				.select(Columns.raw(query)) { filter { eq("order_id", orderId) } }
				.decodeList<OrderItem>()
		}

		suspend fun markFinished(orderId: String, itemId: String) =
			SupabaseClient.db["order_items"]
				.update({ set("item_status", OrderStatus.FINISHED) }) {
					filter {
						eq("order_id", orderId)
						eq("item_id", itemId)
						eq("item_status", OrderStatus.PREPARING)
					}
				}

		suspend fun markCancelled(orderId: String, itemId: String) =
			SupabaseClient.db["order_items"]
				.update({ set("item_status", OrderStatus.CANCELED) }) {
					filter {
						eq("order_id", orderId)
						eq("item_id", itemId)
						eq("item_status", OrderStatus.PREPARING)
					}
				}

		val MOCKS = listOf(
			OrderItem(
				orderId = "order1",
				itemId = "item1",
				branchItem = BranchItemWithCatAndIng(
					branchId = "branch1",
					itemId = "item1",
					categoryId = "cat1",
					category = Category("cat1", "Beverages"),
					itemIngredients = listOf(
						ItemIngredientWithIngredient(
							ingredientId = "ing1",
							ingredient = Ingredient.MOCK,
							itemId = "item1",
							quantity = 0.5,
							unit = "liters"
						),
						ItemIngredientWithIngredient(
							ingredientId = "ing2",
							ingredient = Ingredient.MOCK,
							itemId = "item1",
							quantity = 20.0,
							unit = "grams"
						)
					),
					itemName = "Americano",
					itemDes = "Espresso with hot water",
					price = 300,
					estimatedPrep = "5 minutes"
				),
				quantity = 2,
				subtotal = 600,
				specialNotes = "Less water, please.",
				itemStatus = OrderStatus.PREPARING
			)
		)
	}
}
