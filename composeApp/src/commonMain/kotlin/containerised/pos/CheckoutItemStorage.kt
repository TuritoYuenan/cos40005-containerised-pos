package containerised.pos

import containerised.pos.models.BranchItem
import kotlinx.serialization.Serializable

@Serializable
data class CartEntry(
	val branchItem: BranchItem,
	val count: Int
)

expect object CheckoutItemStorage{
	fun saveItem(item: BranchItem)
	fun decreaseItem(item: BranchItem)
	fun loadItems(): List<CartEntry>
	fun clear()
}
