package containerised.pos.modelTests

import containerised.pos.models.ItemTag
import kotlin.test.Test
import kotlin.test.assertEquals

class ItemTagTest {
	@Test
	fun `test item tag`() {
		val itemTag = ItemTag.MOCK
		assertEquals("item123", itemTag.itemId)
		assertEquals("tag456", itemTag.tagId)
		assertEquals("Bestseller", itemTag.tag?.tagName)
	}
}
