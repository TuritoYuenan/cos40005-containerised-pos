package containerised.pos.models

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class BranchItemTest {
	@Test
	fun `test branch item properties`() {
		val branchItem = BranchItem.MOCK
		assertEquals("branch123", branchItem.branchId)
		assertEquals("item123", branchItem.itemId)
	}

	@Test
	fun `test branch item is out of stock`() {
		val branchItem = BranchItem.MOCK.copy(
			itemIngredients = listOf(
				ItemIngredient(
					ingredient = Ingredient(
						id = "ing123",
						ingredientName = "Tomato",
						currentStock = 5.0,
						minStockLevel = 10.0,
						unit = "grams",
						supplierInfo = JsonObject(
							mapOf(
								"contact" to JsonPrimitive("0929340783"),
								"supplier" to JsonPrimitive("Supplier 1")
							)
						),
						branchId = "branch123",
						isActive = true
					),
					ingredientId = "ing123",
					itemId = "item123",
					quantity = 1.0,
					unit = "grams",
				)
			)
		)

		assertEquals(true, branchItem.isOutOfStock())
	}

	@Test
	fun `test branch item is not out of stock when all ingredients are sufficient`() {
		val branchItem = BranchItem.MOCK.copy(
			itemIngredients = listOf(
				ItemIngredient(
					ingredient = Ingredient(
						id = "ing123",
						ingredientName = "Tomato",
						currentStock = 10.0,
						minStockLevel = 10.0,
						unit = "grams",
						supplierInfo = JsonObject(
							mapOf(
								"contact" to JsonPrimitive("0929340783"),
								"supplier" to JsonPrimitive("Supplier 1")
							)
						),
						branchId = "branch123",
						isActive = true
					),
					ingredientId = "ing123",
					itemId = "item123",
					quantity = 1.0,
					unit = "grams",
				)
			)
		)

		assertFalse(branchItem.isOutOfStock())
	}

	@Test
	fun `test branch item stock check throws when ingredients are not loaded`() {
		val branchItem = BranchItem.MOCK.copy(itemIngredients = null)

		assertFailsWith<IllegalStateException> {
			branchItem.isOutOfStock()
		}
	}
}
