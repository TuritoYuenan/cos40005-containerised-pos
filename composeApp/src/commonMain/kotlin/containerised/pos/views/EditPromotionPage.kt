package containerised.pos.views

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.menu_edit.MultiSelectDropdown
import containerised.pos.components.menu_edit.SwitchField
import containerised.pos.components.menu_edit.millisConverter
import containerised.pos.models.BranchItem
import containerised.pos.models.Category
import containerised.pos.models.Promotion
import containerised.pos.models.Tag
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val jsonFormatter = Json { prettyPrint = true }

@Serializable
data class UIRule(
	var targetType: String = "",
	var selectedIds: List<String> = emptyList(),
	var comboItems: Map<String, Int> = emptyMap(),
	var rewardType: String = "FLAT_DISCOUNT",
	var rewardValue: String = "",
	var rewardItems: Map<String, Int> = emptyMap()
)

@Serializable
data class DayTimeRange(
	val day: String,
	val startTime: String,
	val endTime: String
)

private data class EditPromotionFormState(
	var startDate: String? = null,
	var endDate: String? = null,
	var isActive: Boolean = false,
	var rules: List<UIRule> = emptyList(),
	var daysOfWeek: List<DayTimeRange> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPromotionPage(navController: NavController, promotionId: String?) {
	var formState by remember { mutableStateOf(EditPromotionFormState()) }
	var promotion by remember { mutableStateOf<Promotion?>(null) }
	var categories by remember { mutableStateOf(emptyList<Category>()) }
	var items by remember { mutableStateOf(emptyList<BranchItem>()) }
	var tags by remember { mutableStateOf(emptyList<Tag>()) }

	jsonFormatter.encodeToString(formState.rules)
	jsonFormatter.encodeToString(formState.daysOfWeek)

	LaunchedEffect(promotionId) {
		try {
			if (promotionId != null) {
				promotion = Promotion.fetchById(promotionId)
			}
			promotion?.let {
				formState = EditPromotionFormState(
					startDate = it.startDate,
					endDate = it.endDate,
					isActive = it.isActive,
				)
			}
			categories = Category.fetchAll()
			items = BranchItem.fetchByBranch("BRA26011700")
			tags = Tag.fetchAll()
		} catch (e: Exception) {
			println("Error fetching data: ${e.message}")
		}
	}

	LazyColumn {
		item {
			TopBar { navController.popBackStack() }
		}
		item {
			FormSection(formState) { formState = it }
		}
		item {
			DaysOfWeekSection(formState.daysOfWeek) { newDays ->
				formState = formState.copy(daysOfWeek = newDays)
			}
		}
		item {
			ConditionSection(
				rules = formState.rules,
				onChange = { newRules ->
					formState = formState.copy(rules = newRules)
				},
				categories = categories,
				items = items,
				tags = tags
			)
		}
		item {
			val scope = rememberCoroutineScope()

			Button(
				onClick = {
					scope.launch {
						try {
							val rulesJson = jsonFormatter.encodeToString(formState.rules)
							val daysJson =
								jsonFormatter.encodeToString(formState.daysOfWeek)

							Promotion.insert(
								startDate = formState.startDate,
								endDate = formState.endDate,
								daysOfWeek = daysJson,
								rules = rulesJson,
								isActive = formState.isActive
							)

							println("✅ Promotion inserted")

						} catch (e: Exception) {
							println("❌ ${e.message}")
						}
					}
				},
				Modifier.fillMaxWidth().padding(12.dp)
			) { Text("Create Promotion") }
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBack: () -> Unit) = CenterAlignedTopAppBar(
	navigationIcon = {
		IconButton(onClick = onBack) {
			Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
		}
	},
	title = { Text("Promotion Edit") },
	modifier = Modifier.testTag("topBar")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormSection(
	formState: EditPromotionFormState,
	onFormChange: (EditPromotionFormState) -> Unit
) {
	var showDialog by remember { mutableStateOf(false) }
	var editingStart by remember { mutableStateOf(true) }
	val datePickerState = rememberDatePickerState()

	Card(Modifier.fillMaxWidth().padding(12.dp, 6.dp)) {
		Column(
			Modifier.fillMaxWidth().padding(12.dp),
			Arrangement.spacedBy(10.dp)
		) {
			Row(
				Modifier.fillMaxWidth(),
				Arrangement.spacedBy(12.dp)
			) {
				DateField(
					label = "Start Date",
					date = formState.startDate ?: "",
					onClick = {
						editingStart = true
						showDialog = true
					},
					modifier = Modifier.weight(1f)
				)
				DateField(
					label = "End Date",
					date = formState.endDate ?: "",
					onClick = {
						editingStart = false
						showDialog = true
					},
					modifier = Modifier.weight(1f)
				)
			}
			SwitchField(
				title = "Active",
				description = "Enable or disable this promotion",
				checked = formState.isActive,
				onCheckedChange = {
					onFormChange(formState.copy(isActive = it))
				}
			)
		}
	}
	if (showDialog) {
		DatePickerDialog(
			onDismissRequest = { showDialog = false },
			confirmButton = {
				TextButton(
					onClick = {
						val date = millisConverter(datePickerState.selectedDateMillis)

						if (editingStart) {
							onFormChange(formState.copy(startDate = date))
						} else {
							onFormChange(formState.copy(endDate = date))
						}

						showDialog = false
					}
				) {
					Text("OK")
				}
			},

			dismissButton = {
				TextButton(onClick = { showDialog = false }) {
					Text("Cancel")
				}
			}
		) {
			DatePicker(state = datePickerState)
		}
	}
}

@Composable
fun DateField(
	label: String,
	date: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) = Column(modifier) {
	Text(label, style = MaterialTheme.typography.labelSmall)

	Row(
		Modifier
			.fillMaxWidth()
			.border(
				1.dp,
				MaterialTheme.colorScheme.outline,
				RoundedCornerShape(4.dp)
			)
			.clickable { onClick() }
			.padding(12.dp, 14.dp),
		Arrangement.SpaceBetween
	) {
		Text(date.ifEmpty { "Select date" })
		Icon(Icons.Default.EditCalendar, "Select date")
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionSection(
	rules: List<UIRule>,
	onChange: (List<UIRule>) -> Unit,
	categories: List<Category>,
	items: List<BranchItem>,
	tags: List<Tag>
) {
	Card(Modifier.fillMaxWidth().padding(12.dp, 6.dp)) {
		Column(Modifier.padding(12.dp)) {
			rules.forEachIndexed { index, rule ->
				RuleItem(
					rule = rule,
					categories = categories,
					items = items,
					tags = tags,
					onUpdate = { updated ->
						val newList = rules.toMutableList()
						newList[index] = updated
						onChange(newList)
					},
					onDelete = {
						val newList = rules.toMutableList()
						newList.removeAt(index)
						onChange(newList)
					}
				)
			}

			Spacer(Modifier.height(8.dp))

			Text(
				"New Condition +",
				Modifier
					.clickable { onChange(rules + UIRule()) }
					.padding(4.dp)
			)
		}
	}
}

@Composable
fun RuleItem(
	rule: UIRule,
	categories: List<Category>,
	items: List<BranchItem>,
	tags: List<Tag>,
	onUpdate: (UIRule) -> Unit,
	onDelete: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 6.dp)
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
			.padding(10.dp)
	) {
		Text("Target", style = MaterialTheme.typography.labelMedium)

		SimpleDropdown(
			label = "Target Type",
			value = rule.targetType,
			options = listOf("ITEM", "CATEGORY", "TAG", "COMBO"),
			onChange = {
				onUpdate(
					rule.copy(
						targetType = it,
						selectedIds = emptyList(),
						comboItems = emptyMap()
					)
				)
			}
		)

		Spacer(Modifier.height(8.dp))

		when (rule.targetType) {
			"ITEM" -> MultiSelectDropdown(
				label = "Items",
				items = items.map { it.itemId to it.itemName },
				selected = rule.selectedIds,
				onChange = { onUpdate(rule.copy(selectedIds = it)) }
			)

			"CATEGORY" -> MultiSelectDropdown(
				label = "Categories",
				items = categories.map { it.categoryId to it.categoryName },
				selected = rule.selectedIds,
				onChange = { onUpdate(rule.copy(selectedIds = it)) }
			)

			"TAG" -> MultiSelectDropdown(
				label = "Tags",
				items = tags.map { it.tagId to it.tagName },
				selected = rule.selectedIds,
				onChange = { onUpdate(rule.copy(selectedIds = it)) }
			)

			"COMBO" -> ComboDropdown(
				label = "Combo Items",
				items = items,
				combo = rule.comboItems,
				onChange = { onUpdate(rule.copy(comboItems = it)) }
			)
		}

		Spacer(Modifier.height(8.dp))
		Text("Reward", style = MaterialTheme.typography.labelMedium)

		SimpleDropdown(
			label = "Reward Type",
			value = rule.rewardType,
			options = listOf("GIFT", "FLAT_DISCOUNT", "PERCENTAGE_DISCOUNT"),
			onChange = {
				onUpdate(
					rule.copy(
						rewardType = it,
						rewardValue = "",
						rewardItems = emptyMap()
					)
				)
			}
		)

		Spacer(Modifier.height(8.dp))

		when (rule.rewardType) {
			"FLAT_DISCOUNT", "PERCENTAGE_DISCOUNT" -> {
				OutlinedTextField(
					value = rule.rewardValue,
					onValueChange = { onUpdate(rule.copy(rewardValue = it)) },
					label = {
						Text(
							if (rule.rewardType == "FLAT_DISCOUNT")
								"Amount"
							else
								"Percentage"
						)
					},
					modifier = Modifier.fillMaxWidth()
				)
			}

			"GIFT" -> {
				ComboDropdown(
					label = "Gift Items",
					items = items,
					combo = rule.rewardItems,
					onChange = { onUpdate(rule.copy(rewardItems = it)) }
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboDropdown(
	label: String,
	items: List<BranchItem>,
	combo: Map<String, Int>,
	onChange: (Map<String, Int>) -> Unit
) {
	var expanded by remember { mutableStateOf(false) }
	val itemMap = remember(items) { items.associateBy { it.itemId } }

	val selectedSummary = when {
		combo.isEmpty() -> ""
		combo.size <= 2 -> combo.entries.joinToString {
			val name = itemMap[it.key]?.itemName ?: ""
			"$name x${it.value}"
		}

		else -> "${combo.size} items selected"
	}

	ExposedDropdownMenuBox(
		expanded = expanded,
		onExpandedChange = { expanded = !expanded }
	) {
		OutlinedTextField(
			value = selectedSummary,
			onValueChange = {},
			modifier = Modifier
				.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
				.fillMaxWidth(),
			readOnly = true,
			label = { Text(label) },
			placeholder = { Text("Select items") },
			trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
		)

		ExposedDropdownMenu(
			expanded = expanded,
			onDismissRequest = { expanded = false }
		) {
			items.forEach { item ->
				val qty = combo[item.itemId] ?: 0

				DropdownMenuItem(
					text = {
						Row(
							Modifier.fillMaxWidth(),
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(item.itemName, Modifier.weight(1f))

							OutlinedTextField(
								value = if (qty == 0) "" else qty.toString(),
								onValueChange = { input ->
									val value = input.toIntOrNull() ?: 0
									val newMap = combo.toMutableMap()

									if (value > 0) {
										newMap[item.itemId] = value
									} else {
										newMap.remove(item.itemId)
									}

									onChange(newMap)
								},
								singleLine = true,
								modifier = Modifier.width(70.dp)
							)
						}
					},
					onClick = {} // prevent auto close
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDropdown(
	label: String,
	value: String,
	options: List<String>,
	onChange: (String) -> Unit
) {
	var expanded by remember { mutableStateOf(false) }

	ExposedDropdownMenuBox(
		expanded = expanded,
		onExpandedChange = { expanded = !expanded }
	) {
		OutlinedTextField(
			value = value,
			onValueChange = {},
			readOnly = true,
			label = { Text(label) },
			trailingIcon = {
				ExposedDropdownMenuDefaults.TrailingIcon(expanded)
			},
			modifier = Modifier
				.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
				.fillMaxWidth()
		)

		ExposedDropdownMenu(
			expanded = expanded,
			onDismissRequest = { expanded = false }
		) {
			options.forEach {
				DropdownMenuItem(
					text = { Text(it) },
					onClick = {
						onChange(it)
						expanded = false
					}
				)
			}
		}
	}
}

@Composable
fun DaysOfWeekSection(
	days: List<DayTimeRange>,
	onChange: (List<DayTimeRange>) -> Unit
) {
	val allDays = listOf(
		"MONDAY", "TUESDAY", "WEDNESDAY",
		"THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
	)

	Card(Modifier.fillMaxWidth().padding(12.dp)) {
		Column(Modifier.padding(12.dp)) {
			Text("Active Days", style = MaterialTheme.typography.titleMedium)

			Spacer(Modifier.height(8.dp))

			allDays.forEach { day ->
				val existing = days.find { it.day == day }
				var enabled by remember { mutableStateOf(existing != null) }
				var start by remember { mutableStateOf(existing?.startTime ?: "09:00") }
				var end by remember { mutableStateOf(existing?.endTime ?: "22:00") }

				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 4.dp)
						.border(
							1.dp,
							MaterialTheme.colorScheme.outline,
							RoundedCornerShape(6.dp)
						)
						.padding(8.dp)
				) {
					Row(verticalAlignment = Alignment.CenterVertically) {
						Checkbox(
							checked = enabled,
							onCheckedChange = {
								enabled = it
								updateDays(days, day, enabled, start, end, onChange)
							}
						)

						Text(day, modifier = Modifier.weight(1f))
					}

					if (enabled) {
						Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
							OutlinedTextField(
								value = start,
								onValueChange = {
									start = it
									updateDays(days, day, enabled, start, end, onChange)
								},
								label = { Text("Start") },
								modifier = Modifier.weight(1f)
							)

							OutlinedTextField(
								value = end,
								onValueChange = {
									end = it
									updateDays(days, day, enabled, start, end, onChange)
								},
								label = { Text("End") },
								modifier = Modifier.weight(1f)
							)
						}
					}
				}
			}
		}
	}
}

fun updateDays(
	current: List<DayTimeRange>,
	day: String,
	enabled: Boolean,
	start: String,
	end: String,
	onChange: (List<DayTimeRange>) -> Unit
) {
	val newList = current.toMutableList()
	newList.removeAll { it.day == day }

	if (enabled) newList.add(DayTimeRange(day, start, end))
	onChange(newList)
}
