package containerised.pos.views

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import containerised.pos.components.SelfCheckoutView
import containerised.pos.components.TallCard
import containerised.pos.models.BranchItem
import containerised.pos.models.Order
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class, ExperimentalWasmJsInterop::class)
class CustomerComponentsTest {
	@Test
	fun `test self checkout view displays correctly`() = runComposeUiTest {
		setContent {
            SelfCheckoutView(Order.MOCK)
		}

		onNodeWithTag("selfCheckoutView").assertExists()
		onNodeWithTag("checkoutTitle").assertExists()
		onNodeWithTag("orderAmount").assertExists()
		onNodeWithTag("orderAmount").assertTextContains("10000")
		onNodeWithTag("paymentMethodInfo").assertExists()
		onNodeWithTag("paymentQRCode").assertExists()
		onNodeWithText("Download payment QR code").assertExists()
		onNodeWithText("Self-checkout").assertExists()
	}

	@Test
	fun `test self checkout view displays loading state when order is null`() = runComposeUiTest {
		setContent {
            SelfCheckoutView(null)
		}

		onNodeWithTag("selfCheckoutView").assertExists()
		onNodeWithText("Loading order details...").assertExists()
	}

	@Test
	fun `test tall item card displays key details and action`() = runComposeUiTest {
		setContent {
			BranchItem.MOCK.TallCard { }
		}

		onNodeWithText("Pho Bo").assertExists()
		onNodeWithText("50000 VND").assertExists()
		onNodeWithText("Add to cart").assertExists()
	}
}
