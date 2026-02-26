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
import androidx.compose.material.icons.outlined.Search
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
import containerised.pos.database.SupabaseClientProvider.supabase
import containerised.pos.database.uploadImage
import containerised.pos.models.BranchItem
import containerised.pos.models.Category
import containerised.pos.models.fetchBranchItemById
import containerised.pos.models.fetchCategory
import containerised.pos.models.fetchCategoryById
import containerised.pos.models.updateBranchItem
import containerised.pos.rememberImagePickerBytes
import containerised.pos.rememberImagePickerUri
import io.github.jan.supabase.storage.storage
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun EditItemPage(navController: NavController?) {
//
//    val scope = rememberCoroutineScope()
//
//    // --- Image picker state ---
//    var uri by remember { mutableStateOf<Any?>(null) }
//    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
//
//    val openImagePicker = rememberImagePickerUri { result ->
//        uri = result
//    }
//    imageBytes = rememberImagePickerBytes(uri)
//
//    // --- Data state ---
//    val itemId = "ITM26011701" // predefined item
//    var branchItem by remember { mutableStateOf<BranchItem?>(null) }
//    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
//
//    // --- Form state ---
//    var name by remember { mutableStateOf("") }
//    var price by remember { mutableStateOf("") }
//    var isFeatured by remember { mutableStateOf(false) }
//    var selectedCategoryName by remember { mutableStateOf<Category?>(null) }
//    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
//    var imgUrl by remember { mutableStateOf<String?>(null) }
//
//    // --- Load data ---
//    LaunchedEffect(Unit) {
//        try {
//            branchItem = fetchBranchItemById(itemId)
//            categories = fetchCategory()
//        } catch (e: Exception) {
//            println("Load error: ${e.message}")
//        }
//    }
//
//    // --- Sync UI state when item loads ---
//    LaunchedEffect(branchItem) {
//        branchItem?.let { item ->
//            name = item.itemName
//            price = item.price.toString()
//            isFeatured = item.isFeatured
//            selectedCategoryId = item.categoryId
//            imgUrl = item.urlImg
//
//            item.categoryId?.let { categoryId ->
//                fetchCategoryById(categoryId)?.let {
//                    selectedCategoryName = it.categoryName
//                }
//            }
//        }
//    }
//
//    // --- UI ---
//    LazyColumn {
//        item {
//
//            EditItemTopBar {
//                navController?.popBackStack()
//            }
//
//            EditMenuImageSection(
//                imageBytes = imageBytes,
//                imageUrl = imgUrl,
//                onUploadClick = openImagePicker
//            )
//
//            EditMenuFormSection(
//                name = name,
//                price = price,
//                selectedCategoryName = selectedCategoryName,
//                categories = categories,
//                isFeatured = isFeatured,
//                onNameChange = { name = it },
//                onPriceChange = { price = it },
//                onCategorySelected = {
//                    selectedCategoryName = it.categoryName
//                    selectedCategoryId = it.categoryId
//                },
//                onFeaturedChange = { isFeatured = it }
//            )
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(12.dp),
//                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
//            ) {
//
//                Button(
//                    onClick = {
//                        // TODO: delete logic
//                    },
//                    shape = RoundedCornerShape(8.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.error,
//                        contentColor = Color.White
//                    ),
//                    modifier = Modifier.height(40.dp)
//                ) {
//                    Icon(Icons.Filled.Delete, contentDescription = null)
//                    Spacer(Modifier.width(6.dp))
//                    Text("Delete")
//                }
//
//                Button(
//                    onClick = {
//                        scope.launch {
//
//                            var finalImageUrl = imgUrl
//
//                            if (imageBytes != null) {
//                                val name = List(10) { ('a'..'z').random() }.joinToString("")
//                                uploadImage("menu-images/$name.png", imageBytes!!)
//                                finalImageUrl = supabase.storage
//                                    .from("images")
//                                    .publicUrl("menu-images/$name.png")
//                            }
//
//                            val updatedItem = branchItem?.copy(
//                                itemName = name,
//                                price = price.toFloatOrNull() ?: 0f,
//                                categoryId = selectedCategoryId,
//                                isFeatured = isFeatured,
//                                urlImg = finalImageUrl
//                            )
//
//                            if (updatedItem != null) {
//                                updateBranchItem(itemId, updatedItem)
//                            }
//
//                            navController?.popBackStack()
//                        }
//                    },
//                    shape = RoundedCornerShape(8.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.primary,
//                        contentColor = Color.White
//                    ),
//                    modifier = Modifier.height(40.dp)
//                ) {
//                    Icon(Icons.Filled.Check, contentDescription = null)
//                    Spacer(Modifier.width(6.dp))
//                    Text("Update")
//                }
//            }
//        }
//    }
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
)
{
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
            if (imageBytes != null) {
                Image(
                    bitmap = imageBytes!!.decodeToImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            else if (imageUrl != null){
                val url = imageUrl.toString()
                KamelImage(
                    resource = { asyncPainterResource(url) },
                    contentDescription = "Menu image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            else {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFACACAC)),
                    contentAlignment = Alignment.Center
                ) {}
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { onUploadClick() },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Upload", color = Color.White)
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