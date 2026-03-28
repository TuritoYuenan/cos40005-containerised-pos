package containerised.pos.viewTests

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.compose.rememberNavController
import containerised.pos.views.EditPromotionPage
import containerised.pos.views.UIRule
import kotlin.test.Test
import kotlin.test.assertEquals

class EditPromotionPageTest {
	@Test
	fun `test promotion page UI rules`() {
		val rules = UIRule(
			targetType = "Promotion",
			selectedIds = listOf("promo123"),
			comboItems = mapOf(
				"Buy One Get One Free" to 1,
				"20% Off" to 2,
				"Free Item with Purchase" to 3
			),
			rewardType = "Discount",
			rewardValue = "10.0 percent",
			rewardItems = mapOf(
				"Free Coffee Mug" to 1,
				"Free T-Shirt" to 2
			)
		)

		assertEquals("Promotion", rules.targetType)
		assertEquals(listOf("promo123"), rules.selectedIds)
		assertEquals(3, rules.comboItems.size)
		assertEquals(1, rules.comboItems["Buy One Get One Free"])
		assertEquals(2, rules.comboItems["20% Off"])
		assertEquals(3, rules.comboItems["Free Item with Purchase"])
		assertEquals("Discount", rules.rewardType)
		assertEquals("10.0 percent", rules.rewardValue)
		assertEquals(2, rules.rewardItems.size)
		assertEquals(1, rules.rewardItems["Free Coffee Mug"])
		assertEquals(2, rules.rewardItems["Free T-Shirt"])
	}

	@OptIn(ExperimentalTestApi::class)
	@Test
	fun `test promotion page`() = runComposeUiTest {
		setContent {
			EditPromotionPage(rememberNavController(), "PRO")
		}

		onNodeWithTag("topBar").assertExists()
	}
}
