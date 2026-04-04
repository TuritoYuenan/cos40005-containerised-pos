package containerised.pos.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.BackButton
import containerised.pos.components.menu_edit.MultiSelectDropdown
import containerised.pos.components.menu_edit.SwitchField
import containerised.pos.models.BranchItem
import containerised.pos.models.BranchItemInsert
import containerised.pos.models.Category
import containerised.pos.models.ItemTag
import containerised.pos.models.Tag
import containerised.pos.routes.StaffRoutes
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private data class EditItemFormState(
	var name: String = "",
	var price: String = "0",
	var categoryId: String? = null,
	var isFeatured: Boolean = false,
	var selectedTagIds: Set<String> = setOf(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemPage(navController: NavController, itemId: String? = null) {
	val scope = rememberCoroutineScope()
	var imageUrl by remember { mutableStateOf<String?>(null) }
	var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
	var categories by remember { mutableStateOf(emptyList<Category>()) }
	var tags by remember { mutableStateOf(emptyList<Tag>()) }
	var formState by remember { mutableStateOf(EditItemFormState()) }
	var item by remember { mutableStateOf<BranchItem?>(null) }
	var itemTags by remember { mutableStateOf(emptyList<ItemTag>()) }
	var selectedTagIds by remember { mutableStateOf(setOf<String>()) }
    var originalTagIds by remember { mutableStateOf(setOf<String>()) }
    var branchId = "BRA26011700"
	LaunchedEffect(Unit) {
		try {
			categories = Category.fetchAll()
			tags = Tag.fetchAll()
			if (itemId != null) {
				item = BranchItem.fetchById(itemId)
				itemTags = ItemTag.fetchByItemId(itemId)
                originalTagIds = itemTags.map { it.tagId }.toSet()
                selectedTagIds = originalTagIds
			}
			item?.let {
				formState = EditItemFormState(
					name = it.itemName,
					price = it.price.toString(),
					categoryId = it.categoryId,
					isFeatured = it.isFeatured
				)
			}
		} catch (e: Exception) {
			println("Error fetching data: ${e.message}")
		}
	}

	LazyColumn {
		item {
			TopBar { navController.popBackStack() }
			EditMenuImageSection(imageBytes, imageUrl) {}

			FormSection(
				formState = formState,
				onFormChange = { formState = it },
				categories = categories,
				tags = tags,
				selectedTagIds = selectedTagIds,
				onTagChange = { selectedTagIds = it }
			)

			Row(
				Modifier.fillMaxWidth().padding(12.dp),
				Arrangement.spacedBy(10.dp, Alignment.End)
			) {
				if (itemId == null) {
                    CreateButton {
                        val newItem = BranchItemInsert(
                            branchId = branchId,
                            categoryId = formState.categoryId,
                            itemName = formState.name,
                            itemDes = null,
                            price = formState.price.toInt(),
                            estimatedPrep = "12 min",
                            isAvailable = true,
                            isFeatured = formState.isFeatured
                        )

                        scope.launch {
                            val createdItem = BranchItem.create(newItem)
                            ItemTag.insertTags(
                                itemId = createdItem.itemId,
                                tagIds = selectedTagIds
                            )
                            navController.navigate(StaffRoutes.MenuEdit)
                        }
                    }
				} else {
                    DeleteButton {
                        scope.launch {
                            BranchItem.delete(itemId)
                            navController.navigate(StaffRoutes.MenuEdit)
                        }
                    }
                    UpdateButton {
                        item?.let { original ->
                            val updated = original.copy(
                                itemName = formState.name,
                                price = formState.price.toInt(),
                                categoryId = formState.categoryId,
                                isFeatured = formState.isFeatured
                            )

                            scope.launch {
                                BranchItem.update(itemId, updated)

                                // 🔥 UPDATE TAGS HERE
                                ItemTag.updateTags(
                                    itemId = itemId,
                                    oldTagIds = originalTagIds,
                                    newTagIds = selectedTagIds
                                )

                                navController.navigate(StaffRoutes.MenuEdit)
                            }
                        }
                    }
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        )
    ) {
        Icon(Icons.Filled.Check, contentDescription = "Create")
        Spacer(Modifier.width(6.dp))
        Text("Create")
    }
}

@Composable
private fun UpdateButton(onClick: () -> Unit) = Button(
	onClick = onClick,
	modifier = Modifier.height(40.dp),
	shape = RoundedCornerShape(8.dp),
	colors = ButtonDefaults.buttonColors(
		containerColor = MaterialTheme.colorScheme.primary,
		contentColor = Color.White
	)
) {
	Icon(Icons.Filled.Check, "Update")
	Spacer(Modifier.width(6.dp))
	Text("Update")
}

@Composable
private fun DeleteButton(onClick: () -> Unit) = Button(
	onClick = onClick,
	shape = RoundedCornerShape(8.dp),
	colors = ButtonDefaults.buttonColors(
		containerColor = MaterialTheme.colorScheme.error,
		contentColor = Color.White
	),
	modifier = Modifier.height(40.dp)
) {
	Icon(Icons.Filled.Delete, "Delete")
	Spacer(Modifier.width(6.dp))
	Text("Delete")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBack: () -> Unit) = CenterAlignedTopAppBar(
	title = { Text("Item edit") },
	navigationIcon = { BackButton(onClick = onBack) }
)

@Composable
fun EditMenuImageSection(
	imageBytes: ByteArray?,
	imageUrl: String?,
	onUploadClick: () -> Unit
) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormSection(
	formState: EditItemFormState,
	onFormChange: (EditItemFormState) -> Unit,
	categories: List<Category>,
	tags: List<Tag>,
	selectedTagIds: Set<String>,
	onTagChange: (Set<String>) -> Unit
) {
	var categoryExpanded by remember { mutableStateOf(false) }

	// Build selected tag display text
	remember(selectedTagIds, tags) {
		val selected = tags.filter { selectedTagIds.contains(it.tagId) }
		if (selected.isEmpty()) {
			""
		} else {
			selected
				.take(3)
				.joinToString(", ") { it.tagName }
				.let {
					if (selected.size > 3) "$it +${selected.size - 3} more"
					else it
				}
		}
	}

	Card(Modifier.fillMaxWidth().padding(12.dp, 6.dp)) {
		Column(
			Modifier.fillMaxWidth().padding(12.dp),
			Arrangement.spacedBy(10.dp)
		) {
			OutlinedTextField(
				value = formState.name,
				onValueChange = { onFormChange(formState.copy(name = it)) },
				label = { Text("Name") },
				modifier = Modifier.fillMaxWidth(),
				trailingIcon = {
					Icon(Icons.Default.Edit, contentDescription = null)
				}
			)

			OutlinedTextField(
				value = formState.price,
				onValueChange = { onFormChange(formState.copy(price = it)) },
				label = { Text("Price") },
				modifier = Modifier.fillMaxWidth(),
				trailingIcon = {
					Icon(Icons.Default.Edit, contentDescription = null)
				}
			)

			ExposedDropdownMenuBox(
				expanded = categoryExpanded,
				onExpandedChange = { categoryExpanded = !categoryExpanded }
			) {
				val categoryName = categories
					.find { it.categoryId == formState.categoryId }
					?.categoryName ?: ""

				OutlinedTextField(
					value = categoryName,
					onValueChange = {},
					modifier = Modifier
						.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
						.fillMaxWidth(),
					readOnly = true,
					label = { Text("Category") },
					trailingIcon = {
						ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
					}
				)

				ExposedDropdownMenu(
					expanded = categoryExpanded,
					onDismissRequest = { categoryExpanded = false }
				) {
					categories.forEach {
						DropdownMenuItem(
							text = { Text(it.categoryName) },
							onClick = {
								onFormChange(
									formState.copy(categoryId = it.categoryId)
								)
								categoryExpanded = false
							}
						)
					}
				}
			}

            MultiSelectDropdown(
                label = "Tags",
                items = tags.map { it.tagId to it.tagName },
                selected = selectedTagIds.toList()
            ) { newList -> onTagChange(newList.toSet()) }

			SwitchField(
				title = "Featured Item",
				description = "Highlight this item on the menu",
				checked = formState.isFeatured
			) { onFormChange(formState.copy(isFeatured = it)) }
		}
	}
}

