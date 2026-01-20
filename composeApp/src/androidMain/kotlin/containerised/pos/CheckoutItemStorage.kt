package containerised.pos

import containerised.pos.models.Item

actual object CheckoutItemStorage{
	actual fun saveItems(items: List<Item>) {
	}

	actual fun loadItems(): List<Item>? {
		return null
	}
	actual fun clear(){}

}
