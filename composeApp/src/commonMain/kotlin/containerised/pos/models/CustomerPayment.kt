package containerised.pos.models

import kotlinx.serialization.Serializable

@Serializable
data class CustomerPayment(
	val amount: Double,
	val currency: String
)
