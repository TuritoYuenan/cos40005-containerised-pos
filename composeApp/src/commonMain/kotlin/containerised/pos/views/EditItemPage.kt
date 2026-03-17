package containerised.pos.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import containerised.pos.models.BranchItem
import containerised.pos.models.BranchItem.Companion.update
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import containerised.pos.models.Category
import containerised.pos.models.ItemTag
import containerised.pos.models.Tag
import containerised.pos.routes.StaffRoutes
import kotlinx.coroutines.launch

private data class EditItemFormState(
    var name: String = "",
    var price: String = "0",
    var categoryId: String? = null,
    var isFeatured: Boolean = false
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemPage(
    navController: NavController,
    itemId: String? = null,
) {
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var categories by remember { mutableStateOf(emptyList<Category>()) }
    var tags by remember { mutableStateOf(emptyList<Tag>()) }
    var formState by remember {mutableStateOf(EditItemFormState())}
    var item by remember { mutableStateOf<BranchItem?>(null) }
    var itemTags by remember { mutableStateOf(emptyList<ItemTag>()) }
    var selectedTagIds by remember { mutableStateOf(setOf<String>()) }
    LaunchedEffect(Unit) {
        try {
            categories = Category.fetchAll()
            tags = Tag.fetchAll()
            println(tags)
            if (itemId != null) {
                item = BranchItem.fetchById(itemId)
                itemTags = ItemTag.fetchByItemId(itemId)

                selectedTagIds = itemTags.map { it.tagId }.toSet()
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
            TopBar {
                navController.popBackStack()
            }
            EditMenuImageSection(
                imageBytes = imageBytes,
                imageUrl = imageUrl,
                onUploadClick = {
                }
            )

            FormSection(
                formState = formState,
                onFormChange = { formState = it },
                categories = categories,
                tags = tags,
                selectedTagIds = selectedTagIds,
                onTagChange = { selectedTagIds = it }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
            ) {
                if (itemId == null) {CreateButton(

                )}
                    else {
                        DeleteButton({navController.navigate(StaffRoutes.MenuEdit)})
                        UpdateButton({
                            item?.let { original ->
                                val updated = original.copy(
                                    itemName = formState.name,
                                    price = formState.price.toInt(),
                                    categoryId = formState.categoryId,
                                    isFeatured = formState.isFeatured
                                )
                                scope.launch{BranchItem.update(itemId, updated)}
                            }
                            navController.navigate(StaffRoutes.MenuEdit)
                        })
                    }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateButton(){
    Button(
        onClick = { },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
        modifier = Modifier.height(40.dp)
    ) {
        Icon(Icons.Filled.Check, contentDescription = null)
        Spacer(Modifier.width(6.dp))
        Text("Create")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateButton(
    onClick: () -> Unit,
){
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
        modifier = Modifier.height(40.dp)
    ) {
        Icon(Icons.Filled.Check, contentDescription = null)
        Spacer(Modifier.width(6.dp))
        Text("Update")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteButton(
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = Color.White
        ),
        modifier = Modifier.height(40.dp)
    ) {
        Icon(Icons.Filled.Delete, contentDescription = null)
        Spacer(Modifier.width(6.dp))
        Text("Delete")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        },
        title = { Text("Item edit") }
    )
}

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
    var tagExpanded by remember { mutableStateOf(false) }

    // Build selected tag display text
    val selectedTagNames = remember(selectedTagIds, tags) {
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // 🔹 Name Field
            OutlinedTextField(
                value = formState.name,
                onValueChange = { onFormChange(formState.copy(name = it)) },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            )

            // 🔹 Price Field
            OutlinedTextField(
                value = formState.price,
                onValueChange = { onFormChange(formState.copy(price = it)) },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            )

            // 🔹 Category Dropdown (Single Select)
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                val categoryName =
                    categories.find { it.categoryId == formState.categoryId }?.categoryName ?: ""

                OutlinedTextField(
                    value = categoryName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.categoryName) },
                            onClick = {
                                onFormChange(
                                    formState.copy(categoryId = category.categoryId)
                                )
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // 🔹 Tags Dropdown (Multi Select)
            ExposedDropdownMenuBox(
                expanded = tagExpanded,
                onExpandedChange = { tagExpanded = !tagExpanded }
            ) {
                OutlinedTextField(
                    value = selectedTagNames,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tags") },
                    placeholder = { Text("Select tags") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = tagExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = tagExpanded,
                    onDismissRequest = { tagExpanded = false }
                ) {
                    tags.forEach { tag ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedTagIds.contains(tag.tagId),
                                        onCheckedChange = null
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(tag.tagName)
                                }
                            },
                            onClick = {
                                val newSet =
                                    if (selectedTagIds.contains(tag.tagId))
                                        selectedTagIds - tag.tagId
                                    else
                                        selectedTagIds + tag.tagId

                                onTagChange(newSet)
                            }
                        )
                    }
                }
            }

            // 🔹 Featured Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Featured Item",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Text(
                        text = "Highlight this item on the menu",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = formState.isFeatured,
                    onCheckedChange = {
                        onFormChange(formState.copy(isFeatured = it))
                    }
                )
            }
        }
    }
}