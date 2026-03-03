package containerised.pos.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import containerised.pos.models.Ingredient
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun InventoryPage() {
	val padding = 16.dp
	val scope = rememberCoroutineScope()
	var error by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(true) }
	var ingredients by remember { mutableStateOf<List<Ingredient>>(emptyList()) }
	var searchQuery by remember { mutableStateOf("") }

	suspend fun refreshInventory() {
		try {
			isLoading = true
			ingredients = Ingredient.fetchByBranch("BRA26011700").sortedBy { it.id }
			error = null
		} catch (e: Exception) {
			error = e.message
		} finally {
			isLoading = false
		}
	}

	LaunchedEffect(Unit) { scope.launch { refreshInventory() } }

//	Search
	LaunchedEffect(searchQuery) {
		ingredients = if (searchQuery.isBlank()) {
			Ingredient.fetchByBranch("BRA26011700").sortedBy { it.id }
		} else {
			Ingredient.fetchByBranch("BRA26011700")
				.filter { ingredient ->
					ingredient.ingredientName?.contains(searchQuery, ignoreCase = true) == true ||
						ingredient.unit?.contains(searchQuery, ignoreCase = true) == true
				}
				.sortedBy { it.id }
		}
	}

	val filteredIngredients = remember(ingredients, searchQuery) {
		if (searchQuery.isBlank()) {
			ingredients
		} else {
			ingredients.filter { ingredient ->
				ingredient.ingredientName?.contains(searchQuery, ignoreCase = true) == true ||
					ingredient.unit?.contains(searchQuery, ignoreCase = true) == true
			}
		}
	}

	Column(Modifier.fillMaxSize()) {
		// Search bar
		OutlinedTextField(
			value = searchQuery,
			onValueChange = { searchQuery = it },
			modifier = Modifier.fillMaxWidth().padding(padding),
			placeholder = { Text("Search items by name or unit...") },
			leadingIcon = { Icon(Icons.Filled.Search, "Search") },
			singleLine = true
		)

		if (isLoading) {
			Box(Modifier.fillMaxSize(), Alignment.Center) {
				CircularProgressIndicator()
			}
			return
		}

		if (error != null) {
			Column(
				Modifier.fillMaxSize().padding(padding),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text("Failed to load inventory items\n$error", color = MaterialTheme.colorScheme.error)
			}
			return
		}

		// Ingredients list
		LazyColumn(
			Modifier.padding(horizontal = padding),
			verticalArrangement = Arrangement.spacedBy(padding)
		) {
			items(filteredIngredients.size) { index ->
				IngredientCard(filteredIngredients[index])
			}
		}
	}
}

@Composable
fun IngredientCard(ingredient: Ingredient) {
	OutlinedCard {
		Column(
			Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				Text(
					"${ingredient.ingredientName}",
					style = MaterialTheme.typography.headlineMedium
				)

				Text(
					"${ingredient.currentStock} ${ingredient.unit} in stock",
					style = MaterialTheme.typography.titleMedium
				)
			}

			HorizontalDivider()

			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				Row(horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing)) {
					Icon(Icons.Default.Inventory, "Info")
					Text(
						"Minimum stock level: ${ingredient.minStockLevel} ${ingredient.unit}",
						style = MaterialTheme.typography.bodyMedium
					)
				}

				Row(horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing)) {
					Icon(Icons.Default.LocalShipping, "Info")
					Text(
						formatSupplier(ingredient.supplierInfo),
						style = MaterialTheme.typography.bodyMedium
					)
				}
			}

			HorizontalDivider()

			Row(
				Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
			) {
				var showEditDialog by remember { mutableStateOf(false) }
				var showRemoveDialog by remember { mutableStateOf(false) }

				Button(
					onClick = { showEditDialog = true },
				) {
					Icon(Icons.Outlined.Edit, "Edit")
					Spacer(Modifier.size(ButtonDefaults.IconSpacing))
					Text("Edit")
				}

				TextButton(
					onClick = { showRemoveDialog = true },
					colors = ButtonDefaults.textButtonColors(
						contentColor = MaterialTheme.colorScheme.error
					)
				) {
					Icon(Icons.Outlined.Delete, "Delete")
					Spacer(Modifier.size(ButtonDefaults.IconSpacing))
					Text("Remove")
				}

				if (showEditDialog) EditDialog(ingredient) { showEditDialog = false }
				if (showRemoveDialog) {
					RemoveDialog(ingredient, { showRemoveDialog = false })
					{
						// TODO: Implement remove logic
						showRemoveDialog = false
					}
				}
			}
		}
	}
}

@Composable
fun EditDialog(ingredient: Ingredient, onDismiss: () -> Unit) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text("Edit ${ingredient.ingredientName}") },
		text = { Text("Editing functionality is not implemented yet.") },
		confirmButton = {
			TextButton(onClick = onDismiss) {
				Text("OK")
			}
		}
	)
}

@Composable
fun RemoveDialog(ingredient: Ingredient, onDismiss: () -> Unit, onConfirm: () -> Unit) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text("Remove ${ingredient.ingredientName}?") },
		text = { Text("Are you sure you want to remove this ingredient? This action cannot be undone.") },
		confirmButton = {
			TextButton(onClick = onConfirm) { Text("Remove", color = MaterialTheme.colorScheme.error) }
		},
		dismissButton = {
			TextButton(onClick = onDismiss) { Text("Cancel") }
		}
	)
}

private fun getMockIngredient() = Ingredient(
	branchId = "BRA26011700",
	id = "1",
	ingredientName = "Tomato",
	isActive = true,
	currentStock = 50.0,
	minStockLevel = 10.0,
	unit = "kg",
	supplierInfo = JsonObject(
		mapOf(
			"contact" to JsonPrimitive("0929340783"),
			"supplier" to JsonPrimitive("Supplier 2")
		)
	)
)

private fun formatSupplier(info: JsonObject?): String {
	if (info == null) return "No supplier info"

	val supplierName = info["supplier"]?.jsonPrimitive?.content ?: "Unknown Supplier"
	val contact = info["contact"]?.jsonPrimitive?.content ?: "No contact info"

	return "Supplier: $supplierName ($contact)"
}

@Preview(apiLevel = 35)
@Composable
fun IngredientCardPreview() {
	IngredientCard(getMockIngredient())
}

@Preview(apiLevel = 35)
@Composable
fun EditDialogPreview() {
	EditDialog(getMockIngredient(), onDismiss = {})
}

@Preview(apiLevel = 35)
@Composable
fun RemoveDialogPreview() {
	RemoveDialog(getMockIngredient(), onConfirm = {}, onDismiss = {})
}
