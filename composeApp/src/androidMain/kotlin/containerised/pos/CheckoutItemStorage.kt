package containerised.pos

import containerised.pos.models.BranchItem

actual object CheckoutItemStorage{
	actual fun saveItem(item: BranchItem) {
	}

	actual fun decreaseItem(item: BranchItem){
	}

	actual fun loadItems(): List<CartEntry> {
		return emptyList()
	}
	actual fun clear(){}

}
