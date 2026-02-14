package containerised.pos.routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object CustomerRoutes {
	@Serializable @SerialName("order")
	object Order

	@Serializable @SerialName("checkout")
	object Checkout

	@Serializable @SerialName("payment")
	data class Payment(val amount: Double, val currency: String)
}
