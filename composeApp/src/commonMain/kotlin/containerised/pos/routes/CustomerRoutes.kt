package containerised.pos.routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object CustomerRoutes {
	@Serializable @SerialName("order")
	data class Order(val branchID: String = "Unknown", val tableNumber: String = "Unknown")

	@Serializable @SerialName("checkout")
	data class Checkout(val branchID: String = "Unknown", val tableNumber: String = "Unknown")

	@Serializable @SerialName("payment")
	data class Payment(
		val branchID: String = "Unknown",
		val tableNumber: String = "Unknown",
		val orderID: String = "Unknown",
		val isPayingAtCounter: Boolean = false
	)
}
