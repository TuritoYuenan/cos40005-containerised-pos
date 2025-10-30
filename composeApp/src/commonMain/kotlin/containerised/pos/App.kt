package containerised.pos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import posapplication.composeapp.generated.resources.Res
import posapplication.composeapp.generated.resources.compose_multiplatform

//@Composable
//fun App() {
//    MaterialTheme {
//        var showContent by remember { mutableStateOf(false) }
//        Column(
//            modifier = Modifier
//                .background(MaterialTheme.colorScheme.primaryContainer)
//                .safeContentPadding()
//                .fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
//        }
//    }
//}
@Composable
@Preview
fun SupabaseUIBranchTest() {
	var newBranch by remember { mutableStateOf<Branch?>(null) }
	var branches by remember { mutableStateOf<List<Branch>?>(null) }
	var error by remember { mutableStateOf<String?>(null) }
	LaunchedEffect(Unit) {
		try {
			println("=== Testing Supabase branches table ===")

			// Add a new branch
			 newBranch = Branch(
				branch_name = "Hanoi Central",
				address = "123 Main St, Hanoi",
				phone_number = "+84 912 345 678",
				email = "central@shop.com",
				is_active = true
			)

			addBranch(newBranch!!)
			println("Added branch: ${newBranch!!.branch_name}")

			// Fetch all branches
			 branches = fetchBranches()
			println("Fetched ${branches!!.size} branches:")
			branches!!.forEach { branch ->
				println(
					"• ${branch.branch_id}: ${branch.branch_name} (${branch.email}) - Active: ${branch.is_active}"
				)
			}

		} catch (e: Exception) {
			error = e.message
			println("Error: ${e.message}")
		}
	}
	MaterialTheme {
		Column(
			modifier = Modifier.fillMaxSize().padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			when {
				error != null -> Text("Error: $error")
				branches == null -> CircularProgressIndicator()
				branches!!.isEmpty() -> Text("No branches found.")
				else -> {
					Text("=== Testing Supabase branches table ===")
					Text("Added branch: ${newBranch!!.branch_name}")
					Text("Fetched ${branches!!.size} branches:")
					branches!!.forEach { branch ->
						Text("• ${branch.branch_name} (${branch.email ?: "No email"})")
					}
				}
			}
		}
	}
}
