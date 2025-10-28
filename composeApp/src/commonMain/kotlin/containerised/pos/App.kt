package containerised.pos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import containerised.pos.models.Branch
import containerised.pos.models.MenuItem
import containerised.pos.models.addBranch
import containerised.pos.models.fetchBranches
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import posapplication.composeapp.generated.resources.Res
import posapplication.composeapp.generated.resources.app_name

val list1 = listOf(
	MenuItem("1", "Espresso", "Strong coffee", 4.50, "C1", true, 5, "", "B1", null),
	MenuItem("2", "Cappuccino", "Coffee with milk foam", 4.50, "C1", true, 7, "", "B1", null),
	MenuItem("3", "Latte", "Coffee with steamed milk", 4.50, "C1", true, 6, "", "B1", null)
)
val list2 = listOf(
	MenuItem("4", "Blueberry Muffin", "Fresh muffin with blueberries", 4.50, "C2", true, 10, "", "B1", null),
	MenuItem("5", "Chocolate Croissant", "Flaky croissant with chocolate", 4.50, "C2", true, 12, "", "B1", null),
	MenuItem("6", "Banana Bread", "Moist banana bread slice", 4.50, "C2", true, 8, "", "B1", null)
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun App() {
	MaterialTheme {
		Scaffold(
			topBar = {
				CenterAlignedTopAppBar(
					title = { Text(stringResource(Res.string.app_name)) },
					navigationIcon = {
						IconButton(onClick = {}) {
//							Icon(imageVector = vectorResource(Res.drawable.compose_multiplatform))
						}
					},
					actions = {
						IconButton(onClick = {}) {
//							Icon(imageVector = vectorResource(Res.drawable.compose_multiplatform))
						}
					},
				)
			}
		) { paddingValues ->
			Column(modifier = Modifier.padding(paddingValues)) {
				Text("Lorem", style = MaterialTheme.typography.headlineMedium)
				LazyRow {
					items(list1) { item ->
						OrderPageMenuItem(item.item_name, item.item_price.toString())
					}
				}

				Text("Lorem", style = MaterialTheme.typography.headlineMedium)
				LazyRow {
					items(list2) { item ->
						OrderPageMenuItem(item.item_name, item.item_price.toString())
					}
				}
			}
		}
	}
}

@Composable
fun OrderPageMenuItem(name: String, price: String) {
	Column(
		modifier = Modifier.padding(4.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
//		Image(bitmap = image, contentDescription = name)
		Text(name, style = MaterialTheme.typography.titleLarge)
		Text(price, style = MaterialTheme.typography.bodyLarge)
	}
}

@Composable
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
