package containerised.pos.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.LoadingView
import containerised.pos.models.Ingredient
import containerised.pos.models.StockAdjustment
import containerised.pos.routes.StaffRoutes
import kotlinx.coroutines.launch

private val defaultPadding = 16.dp

/**
 * - Receive Ingredient parameter via navigation
 * - Display all editable fields:
 * 	 + Ingredient Name (TextField)
 * 	 + Unit (TextField or Dropdown)
 * 	 + Minimum Stock Level (TextField, numeric)
 * 	 + Supplier Info (TextFields for supplier name and contact)
 * 	 + Active Status (Switch/Checkbox)
 * - Special quantity adjustment section (see Task 3)
 * - Save/Cancel buttons at bottom
 * - Loading states and error handling
 */
@Composable
fun EditIngredientPage(navController: NavController, args: StaffRoutes.EditIngredient) {
	val scope = rememberCoroutineScope()
	var error by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(true) }
	var ingredient by remember { mutableStateOf<Ingredient?>(null) }

	fun updateIngredient() {
		scope.launch {
			try {
				isLoading = true
				ingredient!!.update()
				error = null
			} catch (e: Exception) {
				error = e.message
			} finally {
				isLoading = false
			}
		}
	}

	LaunchedEffect(args.ingredientId) {
		try {
			isLoading = true
			ingredient = Ingredient.fetchByID(args.ingredientId)
			error = null
		} catch (e: Exception) {
			error = e.message
		} finally {
			isLoading = false
		}
	}

	Column(
		Modifier.fillMaxSize().padding(defaultPadding),
		Arrangement.spacedBy(defaultPadding)
	) {
		if (isLoading) {
			LoadingView(Modifier.fillMaxSize())
			return
		}

		if (error != null) {
			Text(
				"Error: $error",
				Modifier.padding(defaultPadding),
				MaterialTheme.colorScheme.error
			)
			return
		}

		if (ingredient == null) {
			Text(
				"Not found!",
				Modifier.padding(defaultPadding),
				MaterialTheme.colorScheme.error
			)
			return
		}

		EditForm(ingredient!!)

		FormButtons(
			onDelete = { /* TODO: Implement delete functionality */ },
			onSave = { updateIngredient() },
			onCancel = { navController.popBackStack() }
		)
	}
}

@Composable
private fun EditForm(ingredient: Ingredient) {
	Text(
		"Metadata",
		style = MaterialTheme.typography.titleLarge,
	)

	OutlinedTextField(
		ingredient.ingredientName ?: "",
		{ ingredient.ingredientName = it },
		Modifier.fillMaxWidth(),
		label = { Text("Ingredient Name") },
	)

	Row(
		Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(defaultPadding)
	) {
		OutlinedTextField(
			ingredient.minStockLevel?.toString() ?: "",
			{ ingredient.minStockLevel = it.toDoubleOrNull() },
			Modifier.weight(.6f),
			label = { Text("Minimum Stock Level") },
		)

		OutlinedTextField(
			ingredient.unit ?: "",
			{ ingredient.unit = it },
			Modifier.weight(.4f),
			label = { Text("Unit") },
		)
	}

	Text(
		"Current Stock",
		style = MaterialTheme.typography.titleLarge,
	)
	CurrentStockAdjustForm(ingredient)
}

/**
 * Layout:
 * ```
 * Current Stock: [X] kg
 *
 * Adjust Quantity:
 * (Radio button) Set Directly     [_____] kg
 * (Radio button) Add Stock        [_____] kg
 * (Radio button) Remove Stock     [_____] kg
 *
 * Preview: New stock will be [Y] kg
 * ```
 */
@Composable
private fun CurrentStockAdjustForm(ingredient: Ingredient) {
	var adjustmentType by remember { mutableStateOf(StockAdjustment.Type.DIRECT) }
	var adjustmentValue by remember { mutableStateOf("1.0") }

	Row(verticalAlignment = Alignment.CenterVertically) {
		RadioButton(
			adjustmentType == StockAdjustment.Type.DIRECT,
			{ adjustmentType = StockAdjustment.Type.DIRECT },
			Modifier.weight(.2f)
		)
		OutlinedTextField(
			ingredient.currentStock.toString(),
			{ ingredient.currentStock = it.toDoubleOrNull() },
			Modifier.weight(.8f),
			readOnly = adjustmentType != StockAdjustment.Type.DIRECT,
			enabled = adjustmentType == StockAdjustment.Type.DIRECT,
			label = { Text("Current stock level") },
			suffix = { ingredient.unit?.let { Text(it) } }
		)
	}

	Row(verticalAlignment = Alignment.CenterVertically) {
		RadioButton(
			adjustmentType == StockAdjustment.Type.INCREMENT,
			{ adjustmentType = StockAdjustment.Type.INCREMENT },
			Modifier.weight(.2f)
		)
		OutlinedTextField(
			adjustmentValue,
			{ adjustmentValue = it },
			Modifier.weight(.8f),
			readOnly = adjustmentType != StockAdjustment.Type.INCREMENT,
			enabled = adjustmentType == StockAdjustment.Type.INCREMENT,
			label = { Text("Increase stock level") },
			prefix = { Text("${ingredient.currentStock} +") },
			suffix = {
				Text(
					formatAdjustmentResult(
						ingredient.currentStock,
						StockAdjustment.Type.INCREMENT,
						adjustmentValue,
						ingredient.unit
					)
				)
			}
		)
	}

	Row(verticalAlignment = Alignment.CenterVertically) {
		RadioButton(
			adjustmentType == StockAdjustment.Type.DECREMENT,
			{ adjustmentType = StockAdjustment.Type.DECREMENT },
			Modifier.weight(.2f)
		)
		OutlinedTextField(
			adjustmentValue,
			{ adjustmentValue = it },
			Modifier.weight(.8f),
			readOnly = adjustmentType != StockAdjustment.Type.DECREMENT,
			enabled = adjustmentType == StockAdjustment.Type.DECREMENT,
			label = { Text("Decrease stock level") },
			prefix = { Text("${ingredient.currentStock} -") },
			suffix = {
				Text(
					formatAdjustmentResult(
						ingredient.currentStock,
						StockAdjustment.Type.DECREMENT,
						adjustmentValue,
						ingredient.unit
					)
				)
			}
		)
	}
}

/**
 * Formats the preview stock increase/decrease result.
 * For example:
 * + "50.0 + 1.0 = 51.0 grams" then this function supply the "= 51.0 grams" part.
 * + "50.0 - 1.0 = 49.0 grams" then this function supply the "= 49.0 grams" part.
 */
private fun formatAdjustmentResult(current: Double?, type: StockAdjustment.Type, value: String, unit: String?): String {
	// Account when user clears the input or enters invalid number
	val adjustmentNum = value.toDoubleOrNull() ?: return "= $current $unit"

	// Calculate the new stock level based on the adjustment type
	val result = when (type) {
		StockAdjustment.Type.INCREMENT -> current?.plus(adjustmentNum)
		StockAdjustment.Type.DECREMENT -> current?.minus(adjustmentNum)
		else -> null
	} ?: return "= $current $unit"

	// Format the result with unit if available
	return "= $result $unit"
}

@Composable
private fun FormButtons(onDelete: () -> Unit, onSave: () -> Unit, onCancel: () -> Unit) {
	Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp, Alignment.End)) {
		Button(onClick = onSave) { Text("Save") }
		OutlinedButton(onClick = onCancel) { Text("Cancel") }
		IconButton(
			onClick = onDelete,
			colors = IconButtonDefaults.iconButtonColors(
				containerColor = MaterialTheme.colorScheme.errorContainer,
				contentColor = MaterialTheme.colorScheme.onErrorContainer
			)
		) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PagePreview() {
	Column(
		Modifier.fillMaxSize().padding(defaultPadding, 48.dp),
		Arrangement.spacedBy(defaultPadding)
	) {
		EditForm(Ingredient.MOCK)
		FormButtons(onDelete = {}, onSave = {}, onCancel = {})
	}
}
