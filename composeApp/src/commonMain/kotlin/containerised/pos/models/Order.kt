package containerised.pos.models

import containerised.pos.database.SupabaseClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus {
	CANCELED,
	PREPARING,
	FINISHED
}

@Serializable
data class OrderInsert(
	@SerialName("order_id")
	val orderId: String? = null,

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
	val taxAmount: Double? = null,

	@SerialName("final_amount")
	val finalAmount: Int? = null,

	@SerialName("updated_at")
	val updatedAt: String? = null,

	@SerialName("created_at")
	val createdAt: String? = null
) {
	suspend fun add(): String {
		return SupabaseClient.db["orders"]
			.insert(this) { select() }
			.decodeSingle<Order>().orderId
	}

	suspend fun addWithItems(items: List<OrderItemInsert>): String {
		val orderID = add()
		items.forEach { it.add() }
		return orderID
	}
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
	val taxAmount: Double? = null,

	@SerialName("final_amount")
	val finalAmount: Int? = null,

	@SerialName("updated_at")
	val updatedAt: String? = null,

	@SerialName("created_at")
	val createdAt: String? = null
) {
	companion object {
		suspend fun fetchAll(): List<Order> {
			return SupabaseClient.db["orders"].select().decodeList<Order>()
		}

		suspend fun fetchPreparing(): List<Order> {
			return SupabaseClient.db["orders"]
				.select { filter { eq("status", OrderStatus.PREPARING) } }
				.decodeList<Order>()
		}

		suspend fun markFinished(orderId: String) {
			SupabaseClient.db["orders"]
				.update({ set("status", OrderStatus.FINISHED) }) {
					filter {
						eq("order_id", orderId)
						eq("status", OrderStatus.PREPARING)
					}
				}
		}

		suspend fun markCancelled(orderId: String) {
			SupabaseClient.db["orders"]
				.update({ set("status", OrderStatus.CANCELED) }) {
					filter {
						eq("order_id", orderId)
						eq("status", OrderStatus.PREPARING)
					}
				}
		}

		suspend fun fetchByID(orderId: String): Order? {
			return SupabaseClient.db["orders"]
				.select { filter { eq("order_id", orderId) } }
				.decodeSingleOrNull<Order>()
		}
	}
}
