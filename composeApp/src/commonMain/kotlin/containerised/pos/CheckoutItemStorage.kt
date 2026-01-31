package containerised.pos

import containerised.pos.models.BranchItem

expect object CheckoutItemStorage{
	fun saveItems(items: List<BranchItem>)
	fun loadItems(): List<BranchItem>?
	fun clear()
}
