package containerised.pos

import containerised.pos.models.BranchItem
import kotlinx.browser.window
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


actual object CheckoutItemStorage {
	private const val KEY = "items"

	fun save(items: String) {
		window.localStorage.setItem(KEY, items)
	}

	fun load(): String? {
		return window.localStorage.getItem(KEY)
	}
	actual fun saveItem(item: BranchItem) {
		val current = loadItems().toMutableList()

		val index = current.indexOfFirst { it.branchItem.branchId == item.branchId }

		if (index >= 0) {
			val existing = current[index]
			current[index] = existing.copy(count = existing.count + 1)
		} else {
			current.add(CartEntry(item, 1))
		}

		CheckoutItemStorage.save(Json.encodeToString(current))
	}

	actual fun decreaseItem(item: BranchItem) {
		val current = loadItems().toMutableList()

		val index = current.indexOfFirst { it.branchItem.branchId == item.branchId }

		if (index >= 0) {
			val existing = current[index]

			if (existing.count > 1) {
				current[index] = existing.copy(count = existing.count - 1)
			} else {
				// remove when count reaches 0
				current.removeAt(index)
			}

			CheckoutItemStorage.save(Json.encodeToString(current))
		}
	}

	actual fun loadItems(): List<CartEntry> =
		CheckoutItemStorage.load()?.let {
			Json.decodeFromString(it)
		} ?: emptyList()
	actual fun clear(){
		window.localStorage.removeItem(KEY)
	}
}
