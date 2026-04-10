package containerised.pos.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import containerised.pos.components.*
import containerised.pos.routes.StaffRoutes
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class UiComponentsTest {

	@Test
	fun `loading view shows progress indicator`() = runComposeUiTest {
		setContent {
			TestSurface {
				LoadingView(Modifier.fillMaxSize())
			}
		}

		onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).fetchSemanticsNode()
	}

	@Test
	fun `error view shows message and icon`() = runComposeUiTest {
		setContent {
			TestSurface {
				ErrorView("Something went wrong", Modifier.fillMaxSize())
			}
		}

		onNodeWithContentDescription("Error").fetchSemanticsNode()
		onNodeWithText("Something went wrong").fetchSemanticsNode()
	}

	@Test
	fun `create button shows label and invokes callback`() = runComposeUiTest {
		var clicked = false

		setContent {
			TestSurface {
				CreateButton { clicked = true }
			}
		}

		onNodeWithText("Create").assertHasClickAction().performClick()
		assert(clicked)
	}

	@Test
	fun `update button shows label and invokes callback`() = runComposeUiTest {
		var clicked = false

		setContent {
			TestSurface {
				UpdateButton { clicked = true }
			}
		}

		onNodeWithText("Update").assertHasClickAction().performClick()
		assert(clicked)
	}

	@Test
	fun `delete button shows label and invokes callback`() = runComposeUiTest {
		var clicked = false

		setContent {
			TestSurface {
				DeleteButton { clicked = true }
			}
		}

		onNodeWithText("Delete").assertHasClickAction().performClick()
		assert(clicked)
	}

	@Test
	fun `cart status bar shows summary and view cart action`() = runComposeUiTest {
		var viewCartClicked = false

		setContent {
			TestSurface {
				CartStatusBar(3, 15_000) { viewCartClicked = true }
			}
		}

		onNodeWithText("3 items", substring = true).fetchSemanticsNode()
		onNodeWithText("Total: 15000 VND", substring = true).fetchSemanticsNode()
		onNodeWithTag("cartFab").assertHasClickAction().performClick()
		assert(viewCartClicked)
	}

	@Test
	fun `start route falls back to login when permissions do not match`() {
		assertEquals(StaffRoutes.Login, getStartRoute(emptyList()))
	}

	@Test
	fun `start route uses first permitted navigation item`() {
		assertEquals(
			StaffRoutes.MenuEdit,
			getStartRoute(listOf("Menu", "Profile"))
		)
	}

	@Composable
	private fun TestSurface(content: @Composable () -> Unit) {
		AppTheme(darkTheme = false, dynamicColor = false) {
			content()
		}
	}
}
