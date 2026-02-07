package containerised.pos.models

import containerised.pos.database.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
	@SerialName("order_id")
	val orderId: String,

	@SerialName("item_id")
	val itemId: String,

	@SerialName("quantity")
	val quantity: Int,

	@SerialName("subtotal")
	val subtotal: Double? = null,

	@SerialName("special_notes")
	val specialNotes: String? = null,

	@SerialName("item_status")
	val itemStatus: OrderStatus? = null
)

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
