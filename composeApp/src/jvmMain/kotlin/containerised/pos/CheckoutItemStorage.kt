package containerised.pos

import containerised.pos.models.BranchItem

actual object CheckoutItemStorage {
	actual fun addOrIncreaseItem(item: BranchItem) {}
	actual fun removeOrDecreaseItem(item: BranchItem) {}
	actual fun loadItems(): List<CartEntry> = emptyList()
	actual fun clear() {}
}
