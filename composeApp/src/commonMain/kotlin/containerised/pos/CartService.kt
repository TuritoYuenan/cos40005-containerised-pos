package containerised.pos

import containerised.pos.models.BranchItem
import containerised.pos.models.OrderItemInsert
import containerised.pos.models.OrderStatus
import kotlinx.serialization.Serializable

@Serializable
data class CartEntry(
	val branchItem: BranchItem,
	val count: Int,
	val specialNotes: String? = null
) {
	/**
	 * Convert to Order Item table schema
	 */
	fun toOrderItem(orderID: String): OrderItemInsert {
		return OrderItemInsert(
			orderId = orderID,
			itemId = branchItem.itemId,
			quantity = count,
			subtotal = branchItem.price * count,
			specialNotes = specialNotes,
			itemStatus = OrderStatus.PREPARING
		)
	}
}

expect object CartService {
	/**
	 * Saves the given item to the storage.
	 * If the item already exists, it increases the count by 1.
	 */
	fun addOrIncreaseItem(item: BranchItem)

	/**
	 * Decreases the count of the given item by 1.
	 * If the count reaches 0, it removes the item from the storage.
	 */
	fun removeOrDecreaseItem(item: BranchItem)

	/**
	 * Retrieves the special notes for the given item.
	 * If the item does not exist, it returns null.
	 */
	fun getItemNotes(item: BranchItem): String?

	/**
	 * Updates the special notes for the given item.
	 * If the item does not exist, it does nothing.
	 */
	fun updateItemNotes(item: BranchItem, notes: String?)

	/**
	 * Loads the items from the storage and returns them as a list of CartEntry.
	 * If there are no items, it returns an empty list.
	 */
	fun loadItems(): List<CartEntry>

	/**
	 * Clears all items from the storage.
	 */
	fun clear()
}
