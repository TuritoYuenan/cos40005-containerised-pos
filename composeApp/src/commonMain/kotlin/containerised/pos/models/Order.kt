package containerised.pos.models

import containerised.pos.database.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus {
	@SerialName("Preparing")
	PREPARING,

	@SerialName("Finished")
	FINISHED
}

@Serializable
data class Order(
	@SerialName("order_id")
	val orderId: String,

	@SerialName("order_number")
	val orderNumber: String? = null,

	@SerialName("order_type")
	val orderType: String? = null,

	@SerialName("table_number")
	val tableNumber: String? = null,

	@SerialName("status")
	val status: OrderStatus? = null,

	@SerialName("branch_id")
	val branchId: String? = null,

	@SerialName("tax_amount")
	val taxAmount: String? = null,

	@SerialName("final_amount")
	val finalAmount: String? = null,

	@SerialName("updated_at")
	val updatedAt: String? = null,

	@SerialName("created_at")
	val createdAt: String?
)

suspend fun fetchOrder(): List<Order> {
	return SupabaseClientProvider.supabase.postgrest["orders"].select().decodeList<Order>()
}

suspend fun fetchPreparingOrders(): List<Order> {
	return SupabaseClientProvider.supabase.postgrest["orders"]
		.select {
			filter {
				eq("status", "Preparing")
			}
		}
		.decodeList<Order>()
}
