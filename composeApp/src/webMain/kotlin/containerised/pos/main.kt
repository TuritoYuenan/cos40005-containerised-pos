package containerised.pos

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import containerised.pos.views.OrderPage

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        AppNavHost()
//		SupabaseUIBranchTest()
    }
}
