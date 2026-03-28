package containerised.pos.modelTests

import containerised.pos.models.BranchItem
import containerised.pos.models.Ingredient
import containerised.pos.models.ItemIngredientWithIngredient
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

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
				ItemIngredientWithIngredient(
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
}
