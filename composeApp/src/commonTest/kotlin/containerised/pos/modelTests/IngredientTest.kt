package containerised.pos.modelTests

import containerised.pos.models.Ingredient
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

// DO NOT USE `assert()`, it does not exist
// Use `kotlin.test.assertEquals()` or other functions from `kotlin.test` instead
class IngredientTest {
	@Test
	fun testIngredientCreation() {
		val ingredient = Ingredient.MOCK.copy(
			id = "1",
			ingredientName = "Tomato",
			unit = "kg",
			currentStock = 50.0,
			minStockLevel = 10.0,
			supplierInfo = JsonObject(
				mapOf(
					"contact" to JsonPrimitive("0123456789"),
					"supplier" to JsonPrimitive("Fresh Farms"),
				)
			),
			branchId = "1",
			isActive = true
		)

		assertEquals("1", ingredient.id)
		assertEquals("Tomato", ingredient.ingredientName)
		assertEquals("kg", ingredient.unit)
		assertEquals(50.0, ingredient.currentStock)
		assertEquals(10.0, ingredient.minStockLevel)
		assertEquals("1", ingredient.branchId)
		assertEquals(true, ingredient.isActive)
		assertEquals(
			"0123456789",
			ingredient.supplierInfo["contact"]?.jsonPrimitive?.content
		)
		assertEquals(
			"Fresh Farms",
			ingredient.supplierInfo["supplier"]?.jsonPrimitive?.content
		)
	}

	@Test
	fun testLowStock() {
		val ingredient = Ingredient.MOCK.copy(
			currentStock = 5.0,
			minStockLevel = 10.0
		)

		assertEquals(true, ingredient.isLowStock())
	}
}
