package containerised.pos

import containerised.pos.models.BranchItem

actual object CartService {
	actual fun addOrIncreaseItem(item: BranchItem) {}
	actual fun removeOrDecreaseItem(item: BranchItem) {}
	actual fun getItemNotes(item: BranchItem): String? = null
	actual fun updateItemNotes(item: BranchItem, notes: String?) {}
	actual fun loadItems(): List<CartEntry> = emptyList()
	actual fun clear() {}
}
