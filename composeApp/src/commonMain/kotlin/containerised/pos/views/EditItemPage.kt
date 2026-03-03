package containerised.pos.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
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
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import containerised.pos.models.Category
// Dummy Category model for UI-only usage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemPage(
    navController: NavController?,
    item: BranchItem? = null,
) {
    // UI State Only
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategoryName by remember { mutableStateOf("") }
    var isFeatured by remember { mutableStateOf(false) }

    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }


    var categories by remember { mutableStateOf(emptyList<Category>()) }
    LaunchedEffect(Unit) {
        try {
            categories = Category.fetchAll()
        } catch (e: Exception) {
            println("Error fetching data: ${e.message}")
        }
    }
    LazyColumn {
        item {
            EditItemTopBar {
                navController?.popBackStack()
            }
            EditMenuImageSection(
                imageBytes = imageBytes,
                imageUrl = imageUrl,
                onUploadClick = {
                }
            )
            EditMenuFormSection(
                name = name,
                price = price,
                selectedCategoryName = selectedCategoryName,
                categories = categories,
                isFeatured = isFeatured,
                onNameChange = { name = it },
                onPriceChange = { price = it },
                onCategorySelected = {
                    selectedCategoryName = it.categoryName
                },
                onFeaturedChange = { isFeatured = it }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
            ) {
                if (item == null) {CreateButton()}
                    else {
                        DeleteButton()
                        UpdateButton()
                    }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateButton(){
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
fun UpdateButton(){
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
        Text("Update")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteButton(){
    Button(
        onClick = { },
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
fun EditItemTopBar(onBack: () -> Unit) {
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(60.dp)
        ) {

            when {
                imageBytes != null -> {
                    Image(
                        bitmap = imageBytes.decodeToImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                imageUrl != null -> {
                    KamelImage(
                        resource = { asyncPainterResource(imageUrl) },
                        contentDescription = "Menu image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                else -> {
//                    Box(
//                        modifier = Modifier
//                            .size(120.dp)
//                            .clip(RoundedCornerShape(8.dp))
//                            .background(Color(0xFFACACAC)),
//                        contentAlignment = Alignment.Center,
//                        propagateMinConstraints = TODO(),
//                        content =
//                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = onUploadClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Upload")
                }

                Text(
                    text = "Supports PNG, JPEG, WEBP images below 5MB",
                    color = Color.Black.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMenuFormSection(
    name: String,
    price: String,
    selectedCategoryName: String,
    categories: List<Category>,
    isFeatured: Boolean,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onFeaturedChange: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Outlined.Edit, contentDescription = null)
                }
            )

            OutlinedTextField(
                value = price,
                onValueChange = onPriceChange,
                label = { Text("Price") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Outlined.Edit, contentDescription = null)
                }
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.categoryName) },
                            onClick = {
                                onCategorySelected(item)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Is featured",
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isFeatured,
                    onCheckedChange = onFeaturedChange
                )
            }
        }
    }
}