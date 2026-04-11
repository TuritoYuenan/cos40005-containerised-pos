package containerised.pos.views

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.CreateButton
import containerised.pos.components.DeleteButton
import containerised.pos.components.UpdateButton
import containerised.pos.components.menu_edit.ImagePickerCard
import containerised.pos.components.menu_edit.MultiSelectDropdown
import containerised.pos.components.menu_edit.SwitchField
import containerised.pos.components.menu_edit.millisConverter
import containerised.pos.database.SupabaseClient
import containerised.pos.database.SupabaseClient.uploadImage
import containerised.pos.models.*
import containerised.pos.models.User.Companion.fetchBranchById
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private val jsonFormatter = Json { prettyPrint = true }

private data class EditPromotionFormState(
    var promotionName: String = "",
	var startDate: String? = null,
	var endDate: String? = null,
	var isActive: Boolean = false,
	var rules: List<Promotion.Rule> = emptyList(),
	var daysOfWeek: List<DaySchedule> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPromotionPage(navController: NavController, promotionId: String?) {
	var formState by remember { mutableStateOf(EditPromotionFormState()) }
	var promotion by remember { mutableStateOf<Promotion?>(null) }
	var categories by remember { mutableStateOf(emptyList<Category>()) }
	var items by remember { mutableStateOf(emptyList<BranchItem>()) }
	var tags by remember { mutableStateOf(emptyList<Tag>()) }
	var imageUrl by remember { mutableStateOf<String?>(null) }
	var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
	val userId = SupabaseClient.auth.currentUserOrNull()?.id
	var branchId by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(promotionId) {
		try {
			branchId = fetchBranchById(userId?: "")
			if (promotionId != null) {
				promotion = Promotion.fetchById(promotionId)
				imageUrl = promotion?.urlImg
			}
			promotion?.let {
                formState = EditPromotionFormState(
                    promotionName = it.promotionName ?: "",
                    startDate = it.startDate,
                    endDate = it.endDate,
                    isActive = it.isActive,
                    rules = it.rules ?: emptyList(),
                    daysOfWeek = it.daysOfWeek ?: emptyList()
                )
			}
			categories = Category.fetchAll()
			items = BranchItem.fetchByBranch(branchId!!)
			tags = Tag.fetchAll()
		} catch (e: Exception) {
			println("Error fetching data: ${e.message}")
		}
	}

	Column {
		ImagePickerCard(
			imageBytes = imageBytes,
			imageUrl = imageUrl,
			onImageSelected = { bytes ->
				imageBytes = bytes
			}
		)
		LazyColumn {
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

				Row(
					Modifier.fillMaxWidth().padding(12.dp),
					Arrangement.spacedBy(10.dp, Alignment.End)
				) {
					if (promotionId == null) {

						CreateButton(
							onClick = {
								scope.launch {
									try {
										var imgUrl: String? = null
										if (imageBytes != null) {
											val name =
												List(10) { ('a'..'z').random() }.joinToString(
													""
												)
											uploadImage(
												"menu-images/$name.png",
												imageBytes!!
											)
											imgUrl = SupabaseClient.storage
												.from("images")
												.publicUrl("menu-images/$name.png")
										}

										Promotion.insert(
											Promotion.Insertable(
												branchID = branchId!!,
												startDate = formState.startDate,
												endDate = formState.endDate,
												daysOfWeek = formState.daysOfWeek,
												rules = formState.rules,
												isActive = formState.isActive,
												urlImg = imgUrl,
                                                promotionName = formState.promotionName
                                            )
										)
										println("Created")
										navController.popBackStack()
									} catch (e: Exception) {
										println("${e.message}")
									}
								}
							}
						)

					} else {

						DeleteButton(
							onClick = {
								scope.launch {
									try {
										Promotion.deleteById(promotionId)
										println("🗑 Deleted")
										navController.popBackStack()
									} catch (e: Exception) {
										println("${e.message}")
									}
								}
							}
						)

						Spacer(modifier = Modifier.width(8.dp))

						UpdateButton(
							onClick = {
								scope.launch {
									var imgUrl: String? = imageUrl
									if (imageBytes != null) {
										val name =
											List(10) { ('a'..'z').random() }.joinToString(
												""
											)
										uploadImage("menu-images/$name.png", imageBytes!!)
										imgUrl = SupabaseClient.storage
											.from("images")
											.publicUrl("menu-images/$name.png")
									}

									try {
										Promotion.updateById(
											id = promotionId,
											data = Promotion.Insertable(
												branchID = branchId!!,
												startDate = formState.startDate,
												endDate = formState.endDate,
												daysOfWeek = formState.daysOfWeek,
												rules = formState.rules,
												isActive = formState.isActive,
												urlImg = imgUrl,
                                                promotionName = formState.promotionName
											)
										)
										println("Updated")
										navController.popBackStack()
									} catch (e: Exception) {
										println("${e.message}")
									}
								}
							}
						)
					}
				}
			}
		}
	}
}

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
            OutlinedTextField(
                value = formState.promotionName,
                onValueChange = {
                    onFormChange(formState.copy(promotionName = it))
                },
                label = { Text("Promotion Name") },
                modifier = Modifier.fillMaxWidth()
            )
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
	rules: List<Promotion.Rule>,
	onChange: (List<Promotion.Rule>) -> Unit,
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
					.clickable { onChange(rules + Promotion.Rule()) }
					.padding(4.dp)
			)
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

	Box {
		OutlinedTextField(
			value = selectedSummary,
			onValueChange = {},
			readOnly = true,
			modifier = Modifier
				.fillMaxWidth()
				.clickable { expanded = true },
			label = { Text(label) },
			placeholder = { Text("Select items") },
			trailingIcon = {
				IconButton(onClick = { expanded = !expanded }) {
					ExposedDropdownMenuDefaults.TrailingIcon(expanded)
				}
			}
		)

		DropdownMenu(
			expanded = expanded,
			onDismissRequest = { expanded = false },
			modifier = Modifier.fillMaxWidth()
		) {
			items.forEach { item ->
				val qty = combo[item.itemId] ?: 0

				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 12.dp, vertical = 6.dp),
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
						modifier = Modifier.width(80.dp),
						keyboardOptions = KeyboardOptions(
							keyboardType = KeyboardType.Number
						)
					)
				}
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
	days: List<DaySchedule>,
	onChange: (List<DaySchedule>) -> Unit
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
				var enabled by remember(days) { mutableStateOf(existing != null) }
				var start by remember(days) {
					mutableStateOf(
						existing?.startTime ?: "09:00"
					)
				}
				var end by remember(days) { mutableStateOf(existing?.endTime ?: "22:00") }

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

@Composable
fun RuleItem(
	rule: Promotion.Rule,
	categories: List<Category>,
	items: List<BranchItem>,
	tags: List<Tag>,
	onUpdate: (Promotion.Rule) -> Unit,
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

fun updateDays(
	current: List<DaySchedule>,
	day: String,
	enabled: Boolean,
	start: String,
	end: String,
	onChange: (List<DaySchedule>) -> Unit
) {
	val newList = current.toMutableList()
	newList.removeAll { it.day == day }

	if (enabled) newList.add(
		DaySchedule(day, start, end)
	)

	onChange(newList)
}
