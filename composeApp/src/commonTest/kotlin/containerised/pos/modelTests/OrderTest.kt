package containerised.pos.modelTests

import containerised.pos.models.Order
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderTest {
	@Test
	fun `test order is marked finished`() {
		// Given
		val order = Order.MOCK.copy(status = Order.Status.PREPARING)

		// When
		val newOrder = order.copy(status = Order.Status.FINISHED)

		// Then
		val (isPtoF, isPtoC, isFCtoP) = newOrder.inferStatusChange(order)
		assertEquals(
			true, isPtoF,
			"Expected change PREPARING -> FINISHED"
		)
		assertEquals(
			false, isPtoC,
			"Expected no change PREPARING -> CANCELED"
		)
		assertEquals(
			false, isFCtoP,
			"Expected no change FINISHED/CANCELED -> PREPARING"
		)
	}

	@Test
	fun `test order is marked cancelled`() {
		// Given
		val order = Order.MOCK.copy(status = Order.Status.PREPARING)

		// When
		val newOrder = order.copy(status = Order.Status.CANCELED)

		// Then
		val (isPtoF, isPtoC, isFCtoP) = newOrder.inferStatusChange(order)
		assertEquals(
			false, isPtoF,
			"Expected no change PREPARING -> FINISHED"
		)
		assertEquals(
			true, isPtoC,
			"Expected change PREPARING -> CANCELED"
		)
		assertEquals(
			false, isFCtoP,
			"Expected no change FINISHED/CANCELED -> PREPARING"
		)
	}

	@Test
	fun `test order is marked back to preparing`() {
		// Given
		val order = Order.MOCK.copy(status = Order.Status.FINISHED)

		// When
		val newOrder = order.copy(status = Order.Status.PREPARING)

		// Then
		val (isPtoF, isPtoC, isFCtoP) = newOrder.inferStatusChange(order)
		assertEquals(
			false, isPtoF,
			"Expected no change PREPARING -> FINISHED"
		)
		assertEquals(
			false, isPtoC,
			"Expected no change PREPARING -> CANCELED"
		)
		assertEquals(
			true, isFCtoP,
			"Expected change FINISHED/CANCELED -> PREPARING"
		)
	}
}
