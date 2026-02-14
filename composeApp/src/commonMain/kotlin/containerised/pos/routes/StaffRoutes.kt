package containerised.pos.routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object StaffRoutes {
	@Serializable @SerialName("login")
	object Login

	@Serializable @SerialName("inventory")
	object Inventory

	@Serializable @SerialName("menu-edit")
	object MenuEdit

	@Serializable @SerialName("edit-item")
	object EditItem

	@Serializable @SerialName("edit-tag")
	object EditTag

	@Serializable @SerialName("edit-promotion")
	object EditPromotion
}
