package containerised.pos.viewTests

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import containerised.pos.components.*
import containerised.pos.models.BranchItem
import containerised.pos.models.Order

@OptIn(ExperimentalTestApi::class)
class CustomerComponentsTest {
	@Test
	fun `test self checkout view displays correctly`() = runComposeUiTest {
		setContent {
			SelfCheckoutView(Order.MOCK)
		}

		onNodeWithTag("selfCheckoutView").assertExists()
		onNodeWithTag("checkoutTitle").assertExists()
		onNodeWithTag("orderAmount").assertExists()
		onNodeWithTag("paymentMethodInfo").assertExists()
		onNodeWithTag("paymentQRCode").assertExists()
	}

	@Test
	fun `test tall item card displays correctly`() = runComposeUiTest {
		setContent {
			BranchItem.MOCK.TallCard { }
		}

		onNodeWithTag("tallItemCard").assertExists()
		onNodeWithTag("itemName").assertExists()
		onNodeWithTag("itemPrice").assertExists()
		onNodeWithTag("itemImage").assertExists()
	}
}
