package containerised.pos

import containerised.pos.models.Item

expect object CheckoutItemStorage{
	fun saveItems(items: List<Item>)
	fun loadItems(): List<Item>?
	fun clear()
}
