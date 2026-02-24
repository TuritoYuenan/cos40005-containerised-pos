package containerised.pos.models

import containerised.pos.database.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
	@SerialName("order_id")
	val orderId: String,

	@SerialName("item_id")
	val itemId: String,

	val branchItem: BranchItemWithCatAndIng,

	@SerialName("quantity")
	val quantity: Int,

	@SerialName("subtotal")
	val subtotal: Double? = null,

	@SerialName("special_notes")
	val specialNotes: String? = null,

	@SerialName("item_status")
	val itemStatus: OrderStatus? = null
) {
	companion object {


		suspend fun fetchOrderItem(): List<OrderItem> {
			return SupabaseClientProvider.supabase.postgrest["order_items"].select().decodeList<OrderItem>()
		}

		suspend fun fetchOrderItemByOrder(orderId: String): List<OrderItem> {
			val result = SupabaseClientProvider.supabase.postgrest["order_items"]
				.select {
					filter {
						eq("order_id", orderId)
					}
				}
				.decodeList<OrderItem>()
			return result
		}

		suspend fun fetchAndJoinOrderItemByOrder(orderId: String): List<OrderItem> {
			val result = SupabaseClientProvider.supabase.postgrest["order_items"]
				.select(
					columns = Columns.raw(
						"""
					order_id,
					item_id,
					branchItem:branch_items (
						branch_id,
						item_id,
						category_id,
						category:categories (
							category_id,
							category_name
						),
						itemIngredients:item_ingredients(
							ingredient_id,
							ingredient: ingredients(
								ingredient_id,
								ingredient_name,
								unit,
								current_stock
							),
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
					)
				) {
					filter {
						eq("order_id", orderId)
					}
				}
				.decodeList<OrderItem>()
			return result
		}
	}
}
