package containerised.pos

import containerised.pos.models.BranchItem
import kotlinx.browser.window
import kotlinx.serialization.json.Json

actual object CheckoutItemStorage {
	private const val KEY = "items"

	fun save(items: String) {
		window.localStorage.setItem(KEY, items)
	}

	fun load(): String? {
		return window.localStorage.getItem(KEY)
	}
	actual fun saveItems(items: List<BranchItem>) {
		CheckoutItemStorage.save(Json.encodeToString(items))
	}

	actual fun loadItems(): List<BranchItem>? =
		CheckoutItemStorage.load()?.let {
			Json.decodeFromString(it)
		} ?: emptyList()
	actual fun clear(){
		window.localStorage.removeItem(KEY)
	}
}
