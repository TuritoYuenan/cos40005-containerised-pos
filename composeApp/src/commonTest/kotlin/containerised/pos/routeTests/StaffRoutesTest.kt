package containerised.pos.routeTests

import containerised.pos.routes.StaffRoutes
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class StaffRoutesTest {
	private val json = Json.Default

	@Test
	fun `route serial names match expected contract`() {
        assertEquals("login", StaffRoutes.Login.serializer().descriptor.serialName)
        assertEquals("inventory", StaffRoutes.Inventory.serializer().descriptor.serialName)
        assertEquals("ingredient-detail", StaffRoutes.EditIngredient.serializer().descriptor.serialName)
        assertEquals("stock-history", StaffRoutes.StockHistory.serializer().descriptor.serialName)
        assertEquals("menu-edit", StaffRoutes.MenuEdit.serializer().descriptor.serialName)
        assertEquals("edit-item", StaffRoutes.EditItem.serializer().descriptor.serialName)
        assertEquals("edit-promotion", StaffRoutes.EditPromotion.serializer().descriptor.serialName)
        assertEquals("edit-tag", StaffRoutes.EditTag.serializer().descriptor.serialName)
        assertEquals("confirm-order", StaffRoutes.OrderConfirm.serializer().descriptor.serialName)
        assertEquals("kitchen-display", StaffRoutes.KitchenDisplay.serializer().descriptor.serialName)
        assertEquals("setting", StaffRoutes.Setting.serializer().descriptor.serialName)
	}

	@Test
	fun `route serial names are unique`() {
		val serialNames = listOf(
			StaffRoutes.Login.serializer().descriptor.serialName,
			StaffRoutes.Inventory.serializer().descriptor.serialName,
			StaffRoutes.EditIngredient.serializer().descriptor.serialName,
			StaffRoutes.StockHistory.serializer().descriptor.serialName,
			StaffRoutes.MenuEdit.serializer().descriptor.serialName,
			StaffRoutes.EditItem.serializer().descriptor.serialName,
			StaffRoutes.EditPromotion.serializer().descriptor.serialName,
			StaffRoutes.EditTag.serializer().descriptor.serialName,
			StaffRoutes.OrderConfirm.serializer().descriptor.serialName,
			StaffRoutes.KitchenDisplay.serializer().descriptor.serialName,
			StaffRoutes.Setting.serializer().descriptor.serialName,
		)

        assertEquals(serialNames.size, serialNames.toSet().size)
	}

	@Test
	fun `non-nullable args routes throw when decoding without required fields`() {
        assertFailsWith<SerializationException> {
            json.decodeFromString<StaffRoutes.EditIngredient>("{}")
        }

        assertFailsWith<SerializationException> {
            json.decodeFromString<StaffRoutes.StockHistory>("{}")
        }
	}

	@Test
	fun `nullable args routes decode with null when fields are missing`() {
		val editItem = json.decodeFromString<StaffRoutes.EditItem>("{}")
		val editPromotion = json.decodeFromString<StaffRoutes.EditPromotion>("{}")
		val editTag = json.decodeFromString<StaffRoutes.EditTag>("{}")

        assertNull(editItem.itemId)
        assertNull(editPromotion.promotionId)
        assertNull(editTag.tagId)
	}

	@Test
	fun `nullable args routes decode with values when fields are present`() {
		val editItem = StaffRoutes.EditItem(itemId = "item-123")
		val editPromotion = StaffRoutes.EditPromotion(promotionId = "promo-456")
		val editTag = StaffRoutes.EditTag(tagId = "tag-789")

        assertEquals(editItem.itemId, "item-123")
        assertEquals(editPromotion.promotionId, "promo-456")
        assertEquals(editTag.tagId, "tag-789")
	}
}
