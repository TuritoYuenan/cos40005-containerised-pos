package containerised.pos.models

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

	@Test
	fun `test order status unchanged has no inferred transition`() {
		val oldOrder = Order.MOCK.copy(status = Order.Status.PREPARING)
		val newOrder = oldOrder.copy(status = Order.Status.PREPARING)

		val (isPtoF, isPtoC, isFCtoP) = newOrder.inferStatusChange(oldOrder)

		assertEquals(false, isPtoF)
		assertEquals(false, isPtoC)
		assertEquals(false, isFCtoP)
	}

	@Test
	fun `test order cancelled to preparing is inferred as FC to P`() {
		val oldOrder = Order.MOCK.copy(status = Order.Status.CANCELED)
		val newOrder = oldOrder.copy(status = Order.Status.PREPARING)

		val (isPtoF, isPtoC, isFCtoP) = newOrder.inferStatusChange(oldOrder)

		assertEquals(false, isPtoF)
		assertEquals(false, isPtoC)
		assertEquals(true, isFCtoP)
	}
}
