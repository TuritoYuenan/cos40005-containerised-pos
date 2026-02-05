package containerised.pos.models

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
	val subtotal: String? = null,

	@SerialName("special_notes")
	val specialNotes: String? = null,

	@SerialName("item_status")
	val itemStatus: OrderItemStatus? = null
)
@Serializable
enum class OrderItemStatus {
	@SerialName("Preparing")
	PREPARING,

	@SerialName("Finished")
	FINISHED
}
