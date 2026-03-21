package containerised.pos.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.NotificationService
import containerised.pos.RealtimeManager
import containerised.pos.RealtimeServiceController
import containerised.pos.components.ErrorView
import containerised.pos.components.LoadingView
import containerised.pos.models.Ingredient
import containerised.pos.routes.StaffRoutes
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.decodeOldRecord
import io.github.jan.supabase.realtime.decodeRecord
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val CURRENT_BRANCH = "BRA26011700"
private val defaultPadding = 16.dp

@Composable
fun InventoryPage(navController: NavController) {
	var error by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(true) }
	var ingredients by remember { mutableStateOf<List<Ingredient>>(emptyList()) }
	var searchQuery by remember { mutableStateOf("") }

	LaunchedEffect(Unit) {
		try {
			isLoading = true
			ingredients = Ingredient.fetchByBranch(CURRENT_BRANCH).sortedBy { it.id }
			error = null
		} catch (e: Exception) {
			error = e.message
		} finally {
			isLoading = false
		}
	}

	LaunchedEffect(Unit) {
		RealtimeManager.forIngredients.events.collect { action ->
			when (action) {
				is PostgresAction.Insert -> ingredients = ingredients.onChange(action)
				is PostgresAction.Update -> ingredients = ingredients.onChange(action)
				is PostgresAction.Delete -> ingredients = ingredients.onChange(action)
				is PostgresAction.Select -> {}
			}
		}
	}

	val filteredIngredients = remember(ingredients, searchQuery) {
		if (searchQuery.isBlank()) {
			ingredients
		} else {
			ingredients.filter {
				val matchesName = it.ingredientName.contains(searchQuery, true)
				val matchesUnit = it.unit.contains(searchQuery, true)
				matchesName.or(matchesUnit)
			}
		}
	}

	Column(Modifier.fillMaxSize()) {
		// Search bar
		OutlinedTextField(
			value = searchQuery,
			onValueChange = { searchQuery = it },
			modifier = Modifier.fillMaxWidth().padding(defaultPadding),
			placeholder = { Text("Search items by name or unit...") },
			leadingIcon = { Icon(Icons.Filled.Search, "Search") },
			singleLine = true
		)

		if (isLoading) return LoadingView(Modifier.fillMaxSize())

		if (error != null) return ErrorView(
			error ?: "Unknown error",
			Modifier.fillMaxSize()
		)

		// Ingredients list
		LazyColumn(
			Modifier.padding(horizontal = defaultPadding),
			verticalArrangement = Arrangement.spacedBy(defaultPadding)
		) {
			items(filteredIngredients.size) { i ->
				val ingredientID = filteredIngredients[i].id
				filteredIngredients[i].Card(
					{ navController.navigate(StaffRoutes.EditIngredient(ingredientID)) },
					{ navController.navigate(StaffRoutes.StockHistory(ingredientID)) }
				)
			}
		}
	}
}

@Composable
private fun Ingredient.Card(onViewEdit: () -> Unit = {}, onViewHistory: () -> Unit = {}) {
	val ingredient = this
	OutlinedCard {
		Column(Modifier.padding(defaultPadding), Arrangement.spacedBy(8.dp)) {
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				Text(
					ingredient.ingredientName,
					style = MaterialTheme.typography.headlineMedium
				)

				Text(
					"${ingredient.currentStock} ${ingredient.unit} in stock",
					style = MaterialTheme.typography.titleMedium
				)

				if (ingredient.isLowStock()) {
					Row(horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing)) {
						Icon(
							Icons.Default.Warning,
							"Low stock",
							tint = MaterialTheme.colorScheme.error
						)
						Text(
							"Low stock: below minimum level",
							color = MaterialTheme.colorScheme.error,
							style = MaterialTheme.typography.titleSmall
						)
					}
				}
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
					ingredient.supplierInfo?.formatSupplier()?.let {
						Text(
							it,
							style = MaterialTheme.typography.bodyMedium
						)
					}
				}
			}

			HorizontalDivider()

			Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp, Alignment.End)) {
				Button(onViewEdit) { Text("Edit") }
				Button(onViewHistory) { Text("View History") }
			}
		}
	}
}

private fun List<Ingredient>.onChange(action: PostgresAction.Insert): List<Ingredient> {
	val newOne = action.decodeRecord<Ingredient>()
	if (newOne.branchId != CURRENT_BRANCH || !newOne.isActive) return this
	return (this.filterNot { it.id == newOne.id } + newOne).sortedBy { it.id }
}

private fun List<Ingredient>.onChange(action: PostgresAction.Delete): List<Ingredient> {
	val oldOne = action.decodeOldRecord<Ingredient>()
	if (oldOne.branchId != CURRENT_BRANCH) return this
	return this.filterNot { it.id == oldOne.id }
}

private fun List<Ingredient>.onChange(action: PostgresAction.Update): List<Ingredient> {
	val newOne = action.decodeRecord<Ingredient>()
	val oldOne = action.decodeOldRecord<Ingredient>()

	val areAllInBranch = listOf(newOne, oldOne).all { it.branchId == CURRENT_BRANCH }
	val isRecentlyLowStock = newOne.isLowStock() && !oldOne.isLowStock()

	if (areAllInBranch && isRecentlyLowStock) NotificationService.showNotification(
		"Low Stock Alert",
		"${newOne.ingredientName} is below minimum stock"
	)

	return if (newOne.branchId != CURRENT_BRANCH || !newOne.isActive) {
		this.filterNot { it.id == newOne.id }
	} else {
		(this.filterNot { it.id == newOne.id } + newOne).sortedBy { it.id }
	}
}

private fun JsonObject.formatSupplier(): String {
	val supplierName = this["supplier"]?.jsonPrimitive?.content ?: "Unknown Supplier"
	val contact = this["contact"]?.jsonPrimitive?.content ?: "No contact info"

	return "Supplier: $supplierName ($contact)"
}

@Preview(apiLevel = 35)
@Composable
private fun IngredientCardPreview() = Ingredient.MOCK.Card()
