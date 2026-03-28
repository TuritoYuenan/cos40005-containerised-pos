package containerised.pos.services

import containerised.pos.models.BranchItem
import containerised.pos.models.Order
import containerised.pos.models.OrderItem
import kotlinx.browser.window
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Represents a "cart" in the web browser that manages customer order items
 */
object CartService {
	@Serializable
	data class Entry(
		val branchItem: BranchItem,
		val count: Int,
		val specialNotes: String? = null
	) {
		/**
		 * Convert to Order Item table schema
		 */
		fun toOrderItem(orderID: String): OrderItem.Insertable = OrderItem.Insertable(
			orderId = orderID,
			itemId = branchItem.itemId,
			quantity = count,
			subtotal = branchItem.price * count,
			specialNotes = specialNotes,
			itemStatus = Order.Status.PREPARING
		)
	}

	private const val KEY = "items"

	private fun List<Entry>.save() {
		window.localStorage.setItem(KEY, Json.encodeToString<List<Entry>>(this))
	}

	/**
	 * Saves the given item to the storage.
	 * If the item already exists, it increases the count by 1.
	 */
	fun addOrIncreaseItem(item: BranchItem) {
		val current = loadItems().toMutableList()
		val index = current.indexOfFirst { it.branchItem.itemId == item.itemId }

		if (index >= 0) {
			val existing = current[index]
			current[index] = existing.copy(count = existing.count + 1)
		} else {
			current.add(Entry(item, 1))
		}

		current.save()
	}

	/**
	 * Decreases the count of the given item by 1.
	 * If the count reaches 0, it removes the item from the storage.
	 */
	fun removeOrDecreaseItem(item: BranchItem) {
		val current = loadItems().toMutableList()
		val index = current.indexOfFirst { it.branchItem.itemId == item.itemId }

		if (index < 0) return

		val existing = current[index]
		if (existing.count > 1) {
			current[index] = existing.copy(count = existing.count - 1)
		} else {
			// remove when count reaches 0
			current.removeAt(index)
		}

		current.save()
	}

	/**
	 * Updates the special notes for the given item.
	 * If the item does not exist, it does nothing.
	 */
	fun updateItemNotes(item: BranchItem, notes: String?) {
		val current = loadItems().toMutableList()
		val index = current.indexOfFirst { it.branchItem.itemId == item.itemId }

		if (index < 0) return

		val existing = current[index]
		current[index] = existing.copy(specialNotes = notes)

		current.save()
	}

	/**
	 * Retrieves the special notes for the given item.
	 * If the item does not exist, it returns null.
	 */
	fun getItemNotes(item: BranchItem): String? = loadItems().firstOrNull {
		it.branchItem.itemId == item.itemId
	}?.specialNotes

	/**
	 * Calculates the final amount to be paid by the customer, including tax.
	 */
	fun List<Entry>.getFinalAmount(taxAmount: Double): Int {
		val subtotal = this.sumOf { entry -> entry.count * entry.branchItem.price }
		return (subtotal * (1 + taxAmount)).toInt()
	}

	/**
	 * Loads the items from the storage and returns them as a list of CartEntry.
	 * If there are no items, it returns an empty list.
	 */
	fun loadItems(): List<Entry> = window.localStorage.getItem(KEY)
		?.let { Json.decodeFromString<List<Entry>>(it) } ?: emptyList()

	/**
	 * Clears all items from the storage.
	 */
	fun clear() = window.localStorage.removeItem(KEY)
}
