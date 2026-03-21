package containerised.pos.models

import containerised.pos.database.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
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
	suspend fun add(): String = SupabaseClient.db["orders"]
		.insert(this) { select() }
		.decodeSingle<Order>().orderId

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

	@SerialName("table_id")
	val tableID: String? = null,

	val table: Table? = null, // Populated when fetching orders with table details

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
	/**
	 * Infers the status change of an order update action, returning a Triple of three booleans:
	 * 1. Whether the order changed from Preparing to Finished
	 * 2. Whether the order changed from Preparing to Cancelled
	 * 3. Whether the order changed from Finished or Cancelled back to Preparing
	 */
	fun inferStatusChange(old: Order): Triple<Boolean, Boolean, Boolean> {
		val (canceled, preparing, finished) =
			Triple(OrderStatus.CANCELED, OrderStatus.PREPARING, OrderStatus.FINISHED)

		val isPtoF = old.status == preparing && this.status == finished
		val isPtoC = old.status == preparing && this.status == canceled
		val isFCtoP = (old.status == finished || old.status == canceled) && this.status == preparing

		return Triple(isPtoF, isPtoC, isFCtoP)
	}

	companion object {
		suspend fun fetchByBranch(branchID: String): List<Order> = SupabaseClient.db["orders"]
			.select(Columns.raw("*, table:tables (*)")) { filter { eq("branch_id", branchID) } }
			.decodeList<Order>()

		suspend fun fetchPreparing(): List<Order> = SupabaseClient.db["orders"]
			.select(Columns.raw("*, table:tables (*)")) { filter { eq("status", OrderStatus.PREPARING) } }
			.decodeList<Order>()

		suspend fun markFinished(orderId: String) = SupabaseClient.db["orders"]
			.update({ set("status", OrderStatus.FINISHED) }) {
				filter {
					eq("order_id", orderId)
					eq("status", OrderStatus.PREPARING)
				}
			}

		suspend fun markCancelled(orderId: String) = SupabaseClient.db["orders"]
			.update({ set("status", OrderStatus.CANCELED) }) {
				filter {
					eq("order_id", orderId)
					eq("status", OrderStatus.PREPARING)
				}
			}

		suspend fun fetchByID(orderId: String): Order? = SupabaseClient.db["orders"]
			.select { filter { eq("order_id", orderId) } }
			.decodeSingleOrNull<Order>()

		val MOCK = Order(
			orderId = "123",
			orderNumber = "001",
			orderType = "DINE_IN",
			tableID = "TAB26020600",
			table = Table(
				tableId = "TAB26020600",
				tableCode = "7A",
				branchId = "BRA26011700",
			),
			status = OrderStatus.PREPARING,
			branchId = "BRA26011700",
			taxAmount = 0.7,
			finalAmount = 10000,
			updatedAt = "2024-01-01T12:00:00Z",
			createdAt = "2024-01-01T11:00:00Z"
		)
	}
}
