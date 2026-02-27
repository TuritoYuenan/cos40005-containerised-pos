package containerised.pos

import containerised.pos.models.BranchItem
import kotlinx.browser.window
import kotlinx.serialization.json.Json

actual object CartService {
	private const val KEY = "items"

	fun save(items: List<CartEntry>) {
		window.localStorage.setItem(KEY, Json.encodeToString<List<CartEntry>>(items))
	}

	actual fun addOrIncreaseItem(item: BranchItem) {
		val current = loadItems().toMutableList()
		val index = current.indexOfFirst { it.branchItem.itemId == item.itemId }

		if (index >= 0) {
			val existing = current[index]
			current[index] = existing.copy(count = existing.count + 1)
		} else {
			current.add(CartEntry(item, 1))
		}

		save(current)
	}

	actual fun removeOrDecreaseItem(item: BranchItem) {
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

		save(current)
	}

	actual fun loadItems(): List<CartEntry> {
		return window.localStorage.getItem(KEY)
			?.let { Json.decodeFromString<List<CartEntry>>(it) } ?: emptyList()
	}

	actual fun clear() = window.localStorage.removeItem(KEY)
}
