package containerised.pos.routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represent a route on the customer-facing web application.
 * The app must always know where the customer is (which branch, which table).
 */
interface CustomerRoutes {
	val branchID: String
	val tableID: String

	@Serializable
	@SerialName("order")
	data class Order(
		override val branchID: String = "Unknown",
		override val tableID: String = "Unknown"
	) : CustomerRoutes

	@Serializable
	@SerialName("checkout")
	data class Checkout(
		override val branchID: String = "Unknown",
		override val tableID: String = "Unknown"
	) : CustomerRoutes

	@Serializable
	@SerialName("payment")
	data class Payment(
		override val branchID: String = "Unknown",
		override val tableID: String = "Unknown",
		val orderID: String = "Unknown",
		val isPayingAtCounter: Boolean = false
	) : CustomerRoutes
}
