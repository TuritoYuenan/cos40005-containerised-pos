package containerised.pos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
//        App()
		SupabaseUIBranchTest()
    }
}
//@Composable
//fun SupabaseBranchTest() {
//	LaunchedEffect(Unit) {
//		try {
//			println("=== Testing Supabase branches table ===")
//
//			// Add a new branch
//			val newBranch = Branch(
//				branch_name = "Hanoi Central",
//				address = "123 Main St, Hanoi",
//				phone_number = "+84 912 345 678",
//				email = "central@shop.com",
//				is_active = true
//			)
//
//			addBranch(newBranch)
//			println("Added branch: ${newBranch.branch_name}")
//
//			// Fetch all branches
//			val branches = fetchBranches()
//			println("Fetched ${branches.size} branches:")
//			branches.forEach { branch ->
//				println(
//					"• ${branch.branch_id}: ${branch.branch_name} (${branch.email}) - Active: ${branch.is_active}"
//				)
//			}
//
//		} catch (e: Exception) {
//			println("Error: ${e.message}")
//		}
//	}
//}
