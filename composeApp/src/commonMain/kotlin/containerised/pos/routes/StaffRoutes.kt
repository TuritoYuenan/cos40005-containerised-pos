package containerised.pos.routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object StaffRoutes {
	@Serializable
	@SerialName("login")
	object Login

	@Serializable
	@SerialName("inventory")
	object Inventory

	@Serializable
	@SerialName("ingredient-detail")
	data class EditIngredient(val ingredientId: String)

	@Serializable
	@SerialName("stock-history")
	data class StockHistory(val ingredientId: String)

	@Serializable
	@SerialName("menu-edit")
	object MenuEdit

	@Serializable
	@SerialName("edit-item")
	data class EditItem(val itemId: String? = null)

	@Serializable
	@SerialName("edit-promotion")
	data class EditPromotion(val promotionId: String? = null)

	@Serializable
	@SerialName("edit-tag")
	data class EditTag(val tagId: String? = null)

	@Serializable
	@SerialName("confirm-order")
	object OrderConfirm

	@Serializable
	@SerialName("kitchen-display")
	object KitchenDisplay

	@Serializable
	@SerialName("sales")
	object Sales

	@Serializable
	@SerialName("sales-report")
	data class SalesReport(val reportId: String)

	@Serializable @SerialName("employee-management")
	object EmployeeManagement

	@Serializable @SerialName("setting")
	object Setting
}
