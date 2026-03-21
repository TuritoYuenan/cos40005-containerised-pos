package containerised.pos.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.ErrorView
import containerised.pos.components.LoadingView
import containerised.pos.models.Ingredient
import containerised.pos.models.StockAdjustment
import containerised.pos.routes.StaffRoutes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import posapplication.composeapp.generated.resources.*

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

	// These two are for back-end operations and error/loading states
	var error by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(true) }

	// Current ingredient data and form data.
	var ingredient by remember { mutableStateOf<Ingredient?>(null) }
	var formData by remember { mutableStateOf(Ingredient.Insertable(args.ingredientId)) }

	// Any possible errors from form validation
	var formErrors by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }

	/**
	 * On clicking "Save"
	 * 1. If there are validation errors, show them in the form and do not proceed.
	 * 2. If no validation errors, call formData.update(ingredient.id) to save changes to backend.
	 * 3. Handle loading and error states during the update process.
	 * 4. On successful update, navigate back or show success message.
	 */
	fun onUpdate() {
		scope.launch {
			try {
				// Realistically the "Save" button will be blocked
				if (!formData.isSavable(ingredient, formErrors)) return@launch

				error = null
				isLoading = true

				// Update ingredient with new data
				formData.update(args.ingredientId)
				navController.popBackStack()
			} catch (e: Exception) {
				error = e.message
			} finally {
				isLoading = false
			}
		}
	}

	fun onDelete() {
		scope.launch {
			try {
				isLoading = true
				ingredient!!.markActive(false)
				error = null
				navController.popBackStack()
			} catch (e: Exception) {
				error = e.message
			} finally {
				isLoading = false
			}
		}
	}

	// Check for validation errors on form data change
	LaunchedEffect(formData) {
		formErrors = formData.getValidationErrors()
	}

	LaunchedEffect(args.ingredientId) {
		try {
			isLoading = true
			val id = args.ingredientId
			ingredient = Ingredient.fetchByID(id)
			formData = ingredient?.toInsertable() ?: Ingredient.Insertable(id)
			error = null
		} catch (e: Exception) {
			error = e.message
		} finally {
			isLoading = false
		}
	}

	Column(
		Modifier.fillMaxSize().padding(defaultPadding)
			.verticalScroll(rememberScrollState()),
		Arrangement.spacedBy(defaultPadding)
	) {
		if (isLoading) return LoadingView(Modifier.fillMaxSize())

		if (!error.isNullOrBlank()) return ErrorView(
			error!!,
			Modifier.fillMaxSize()
		)

		if (ingredient == null) return ErrorView(
			"Ingredient not found",
			Modifier.fillMaxSize()
		)

		formData.EditForm(formErrors) { formData = it }

		ActionButtons(
			onDelete = { onDelete() },
			onSave = { onUpdate() },
			onCancel = { navController.popBackStack() },
			isSavable = formData.isSavable(ingredient, formErrors)
		)
	}
}

/**
 * Checks if the form is valid and can be saved.
 * This can be used to enable/disable the "Save" button.
 * Savable if no validation errors + form data is new compared to original ingredient data.
 */
private fun Ingredient.Insertable.isSavable(
	original: Ingredient?,
	errors: Map<String, String?>
): Boolean {
	// If there are validation errors, form is not savable
	if (errors.isNotEmpty()) return false

	// If ingredient data is not loaded yet, form is not savable
	if (original == null) return false

	// Check if form data has changes compared to original ingredient data
	return hasChanged(original)
}

@Composable
private fun Ingredient.Insertable.EditForm(
	formErrors: Map<String, String?>,
	onFormDataChange: (Ingredient.Insertable) -> Unit
) {
	var minStockLevelInput by remember(id) {
		mutableStateOf(minStockLevel?.toString().orEmpty())
	}

	Text("Metadata", style = MaterialTheme.typography.titleLarge)

	OutlinedTextField(
		name ?: "",
		{ onFormDataChange(copy(name = it)) },
		Modifier.fillMaxWidth(),
		label = { Text(stringResource(Res.string.fl_ingredient_name)) },
		isError = formErrors["ingredientName"] != null,
		supportingText = { Text(formErrors["ingredientName"] ?: "") }
	)

	Row(
		Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(defaultPadding)
	) {
		OutlinedTextField(
			minStockLevelInput,
			{
				minStockLevelInput = it
				onFormDataChange(copy(minStockLevel = it.toDoubleOrNull()))
			},
			Modifier.weight(.6f),
			label = { Text(stringResource(Res.string.fl_min_stock)) },
			isError = formErrors["minStockLevel"] != null,
			supportingText = { Text(formErrors["minStockLevel"] ?: "") },
		)

		OutlinedTextField(
			unit ?: "",
			{ onFormDataChange(copy(unit = it)) },
			Modifier.weight(.4f),
			label = { Text(stringResource(Res.string.fl_stock)) },
			isError = formErrors["unit"] != null,
			supportingText = { Text(formErrors["unit"] ?: "") },
		)
	}

	Column(
		Modifier.fillMaxWidth(),
		Arrangement.spacedBy(defaultPadding / 4),
	) {
		Text("Current Stock", style = MaterialTheme.typography.titleLarge)
		if (formErrors["currentStock"] != null) Text(
			formErrors["currentStock"] ?: "",
			color = MaterialTheme.colorScheme.error,
			style = MaterialTheme.typography.bodyMedium
		)
	}

	StockAdjustForm(currentStock, unit) { onFormDataChange(copy(currentStock = it)) }
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
private fun StockAdjustForm(
	currentStock: Double?,
	unit: String?,
	onCurrentStockChange: (Double?) -> Unit
) {
	var adjustmentType by remember { mutableStateOf(StockAdjustment.Type.DIRECT) }
	var adjustmentValue by remember { mutableStateOf("1.0") }
	var directStockInput by remember {
		mutableStateOf(
			currentStock?.toString().orEmpty()
		)
	}

	LaunchedEffect(currentStock) {
		directStockInput = currentStock?.toString().orEmpty()
	}

	Row(verticalAlignment = Alignment.CenterVertically) {
		RadioButton(
			adjustmentType == StockAdjustment.Type.DIRECT,
			{ adjustmentType = StockAdjustment.Type.DIRECT },
			Modifier.weight(.2f)
		)
		OutlinedTextField(
			directStockInput,
			{
				directStockInput = it
				onCurrentStockChange(it.toDoubleOrNull())
			},
			Modifier.weight(.8f),
			readOnly = adjustmentType != StockAdjustment.Type.DIRECT,
			enabled = adjustmentType == StockAdjustment.Type.DIRECT,
			label = { Text(stringResource(Res.string.fl_stock)) },
			suffix = { unit?.let { Text(it) } }
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
			label = { Text(stringResource(Res.string.fl_stock_inc)) },
			prefix = { Text("$currentStock +") },
			suffix = {
				Text(
					currentStock.adjustmentResult(
						StockAdjustment.Type.INCREMENT,
						adjustmentValue,
						unit
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
			label = { Text(stringResource(Res.string.fl_stock_dec)) },
			prefix = { Text("$currentStock -") },
			suffix = {
				Text(
					currentStock.adjustmentResult(
						StockAdjustment.Type.DECREMENT,
						adjustmentValue,
						unit
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
private fun Double?.adjustmentResult(
	type: StockAdjustment.Type,
	amount: String,
	unit: String?
): String {
	// Account when user clears the input or enters invalid number
	val adjustmentNum = amount.toDoubleOrNull() ?: return "= $this $unit"

	// Calculate the new stock level based on the adjustment type
	val result = when (type) {
		StockAdjustment.Type.INCREMENT -> this?.plus(adjustmentNum)
		StockAdjustment.Type.DECREMENT -> this?.minus(adjustmentNum)
		else -> null
	} ?: return "= $this $unit"

	// Format the result with unit if available
	return "= $result $unit"
}

@Composable
private fun ActionButtons(
	onDelete: () -> Unit,
	onSave: () -> Unit,
	onCancel: () -> Unit,
	isSavable: Boolean = false
) {
	Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp, Alignment.End)) {
		Button(onClick = onSave, enabled = isSavable) {
			Text(stringResource(Res.string.btn_save))
		}

		OutlinedButton(onClick = onCancel) {
			Text(stringResource(Res.string.btn_cancel))
		}

		IconButton(
			onClick = onDelete,
			colors = IconButtonDefaults.iconButtonColors(
				containerColor = MaterialTheme.colorScheme.errorContainer,
				contentColor = MaterialTheme.colorScheme.onErrorContainer
			)
		) {
			Icon(
				Icons.Default.Delete,
				stringResource(Res.string.btn_delete)
			)
		}
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PagePreview() = Column(
	Modifier.fillMaxSize().padding(defaultPadding, 48.dp),
	Arrangement.spacedBy(defaultPadding)
) {
	Ingredient.MOCK.toInsertable().EditForm(
		mapOf(
			"ingredientName" to stringResource(Res.string.ve_ingredient_name),
			"unit" to stringResource(Res.string.ve_unit),
			"minStockLevel" to stringResource(Res.string.ve_neg_stock),
			"currentStock" to stringResource(Res.string.ve_neg_stock)
		)
	) {}
	ActionButtons(onDelete = {}, onSave = {}, onCancel = {})
}
