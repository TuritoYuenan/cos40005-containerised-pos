package containerised.pos

import containerised.pos.models.BranchItem

actual object CheckoutItemStorage{
	actual fun saveItems(items: List<BranchItem>) {
	}

	actual fun loadItems(): List<BranchItem>? {
		return null
	}
	actual fun clear(){}
}
