package containerised.pos.services

import containerised.pos.models.BranchItem
import containerised.pos.models.Order
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CartServiceTest {
	@BeforeTest
	fun setUp() {
		CartService.clear()
	}

	@AfterTest
	fun tearDown() {
		CartService.clear()
	}

	@Test
	fun loadItemsReturnsEmptyWhenCartIsCleared() {
		CartService.clear()

		assertEquals(emptyList(), CartService.loadItems())
	}

	@Test
	fun addOrIncreaseItemAddsNewEntryAndThenIncrementsCount() {
		val item = sampleItem(itemId = "item-1", price = 12000)

		CartService.addOrIncreaseItem(item)
		CartService.addOrIncreaseItem(item)

		val loaded = CartService.loadItems()
		assertEquals(1, loaded.size)
		assertEquals("item-1", loaded.first().branchItem.itemId)
		assertEquals(2, loaded.first().count)
	}

	@Test
	fun removeOrDecreaseItemDecrementsAndRemovesEntryAtZero() {
		val item = sampleItem(itemId = "item-2", price = 5000)

		CartService.addOrIncreaseItem(item)
		CartService.addOrIncreaseItem(item)
		CartService.removeOrDecreaseItem(item)
		assertEquals(1, CartService.loadItems().first().count)

		CartService.removeOrDecreaseItem(item)
		assertEquals(emptyList(), CartService.loadItems())
	}

	@Test
	fun removeOrDecreaseItemDoesNothingWhenItemDoesNotExist() {
		CartService.removeOrDecreaseItem(sampleItem(itemId = "missing"))

		assertEquals(emptyList(), CartService.loadItems())
	}

	@Test
	fun updateItemNotesAndGetItemNotesPersistExpectedValue() {
		val item = sampleItem(itemId = "item-3")
		CartService.addOrIncreaseItem(item)

		CartService.updateItemNotes(item, "No onion")

		assertEquals("No onion", CartService.getItemNotes(item))
		assertEquals("No onion", CartService.loadItems().first().specialNotes)
	}

	@Test
	fun getItemNotesReturnsNullWhenItemNotInCart() {
		assertNull(CartService.getItemNotes(sampleItem(itemId = "unknown")))
	}

	@Test
	fun getFinalAmountAppliesTaxToSubtotal() {
		val entries = listOf(
			CartService.Entry(sampleItem(itemId = "a", price = 10000), count = 2),
			CartService.Entry(sampleItem(itemId = "b", price = 5000), count = 1)
		)

		val finalAmount = with(CartService) { entries.getFinalAmount(0.2) }

		assertEquals(30000, finalAmount)
	}

	@Test
	fun toOrderItemMapsEntryFieldsCorrectly() {
		val item = sampleItem(itemId = "item-4", price = 7000)
		val entry = CartService.Entry(branchItem = item, count = 3, specialNotes = "Less sugar")

		val orderItem = entry.toOrderItem(orderID = "order-1")

		assertEquals("order-1", orderItem.orderId)
		assertEquals("item-4", orderItem.itemId)
		assertEquals(3, orderItem.quantity)
		assertEquals(21000, orderItem.subtotal)
		assertEquals("Less sugar", orderItem.specialNotes)
		assertEquals(Order.Status.PREPARING, orderItem.itemStatus)
	}

	private fun sampleItem(itemId: String, price: Int = 10000): BranchItem = BranchItem(
		branchId = "branch-1",
		itemId = itemId,
		itemName = "Sample $itemId",
		price = price,
		estimatedPrep = "10 mins",
		isAvailable = true
	)
}
