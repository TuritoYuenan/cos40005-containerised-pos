package containerised.pos.routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object CustomerRoutes {
	@Serializable @SerialName("order")
	data class Order(val tableNumber: String? = null)

	@Serializable @SerialName("checkout")
	object Checkout

	@Serializable @SerialName("payment")
	data class Payment(val orderID: String?, val isPayingAtCounter: Boolean)
}
