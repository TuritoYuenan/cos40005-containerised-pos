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
import containerised.pos.database.SupabaseClient
import containerised.pos.models.BranchItem
import containerised.pos.models.Category
import containerised.pos.rememberImagePickerBytes
import containerised.pos.rememberImagePickerUri
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemPage(navController: NavController?) {
	val scope = rememberCoroutineScope()
	var uri by remember { mutableStateOf<Any?>(null) }
	var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
	val openImagePicker = rememberImagePickerUri { result ->
		println("Picked image: $result")
		uri = result
	}
	imageBytes = rememberImagePickerBytes(uri)
//	println("Image bytes size = ${imageBytes?.size}")

	val itemId = "ITM26011701"    //Editing a predefined item
	var branchItem by remember { mutableStateOf<BranchItem?>(null) }
	var category by remember { mutableStateOf<List<Category>>(emptyList()) }

	var selected by remember { mutableStateOf("") }
	var selectedId by remember { mutableStateOf("") }
	var expanded by remember { mutableStateOf(false) }
	LaunchedEffect(Unit) {
		try {
			branchItem = BranchItem.fetchById(itemId)    //Editing a predefined item
			category = Category.fetchAll()
			val value: String? = branchItem?.categoryId
			if (value != null) {
				selected = Category.fetchById(value)?.categoryName.toString()
			}
		} catch (e: Exception) {
			val error = e.message
			println("Error: $error")
		}
	}

	var price by remember { mutableStateOf(branchItem?.price ?: 0) }
	var isFeatured by remember { mutableStateOf(branchItem?.isFeatured) }
	var imgUrl by remember { mutableStateOf(branchItem?.urlImg) }

	LazyColumn {
		item {
			CenterAlignedTopAppBar(
				navigationIcon = {
					IconButton(onClick = { navController?.popBackStack() }) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Back"
						)
					}
				},
				title = { Text("Item edit") }
			)
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
					} else if (imgUrl != null) {
						val url = imgUrl.toString()
						KamelImage(
							resource = { asyncPainterResource(url) },
							contentDescription = "Menu image",
							modifier = Modifier
								.size(120.dp)
								.clip(RoundedCornerShape(8.dp))
						)
					} else {
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
							onClick = { openImagePicker() },
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
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 6.dp, horizontal = 12.dp),
			) {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 6.dp, horizontal = 12.dp),
					verticalArrangement = Arrangement.spacedBy(6.dp)
				) {
					OutlinedTextField(
						value = "Input",
						onValueChange = {},
						label = { Text("Name") },
						singleLine = true,
						modifier = Modifier.fillMaxWidth(),
						trailingIcon = {
							Icon(
								imageVector = Icons.Outlined.Edit,
								contentDescription = "Action"
							)
						}
					)

					OutlinedTextField(
						value = price.toString(),
						onValueChange = { price = it.toIntOrNull() ?: price },
						label = { Text("Price") },
						singleLine = true,
						modifier = Modifier.fillMaxWidth(),
						trailingIcon = {
							Icon(
								imageVector = Icons.Outlined.Edit,
								contentDescription = "Action"
							)
						}
					)
					ExposedDropdownMenuBox(
						expanded = expanded,
						onExpandedChange = { expanded = !expanded }
					) {
						OutlinedTextField(
							label = { Text("Category") },
							value = selected,
							onValueChange = {},
							readOnly = true,
							modifier = Modifier
								.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
								.fillMaxWidth(),
							trailingIcon = {
								ExposedDropdownMenuDefaults.TrailingIcon(expanded)
							}
						)
						ExposedDropdownMenu(
							expanded = expanded,
							onDismissRequest = { expanded = false }
						) {
							category.forEach { item ->
								DropdownMenuItem(
									text = { Text(text = item.categoryName) },
									onClick = {
										selected = item.categoryName
										selectedId = item.categoryId
										expanded = false
									}
								)
							}
						}
					}
					OutlinedTextField(
						value = "Type to find tags",
						onValueChange = {},
						label = { Text("Tags") },
						singleLine = true,
						modifier = Modifier.fillMaxWidth(),
						trailingIcon = {
							Icon(
								imageVector = Icons.Outlined.Search,
								contentDescription = "Action"
							)
						}
					)
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = 4.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						Text(
							text = "Is featured",
							modifier = Modifier.weight(1f)
						)
						RadioButton(
							selected = isFeatured == true,
							onClick = { isFeatured = !isFeatured!! }
						)
					}
				}
			}
			Row(
				horizontalArrangement = Arrangement.spacedBy(
					10.dp,
					Alignment.End
				),
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 6.dp, horizontal = 12.dp),
			) {
				Button(
					onClick = { },
					shape = RoundedCornerShape(8.dp),
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primary,
						contentColor = Color.White
					),
					modifier = Modifier.height(40.dp)
				) {
					Icon(
						Icons.Filled.Delete,
						contentDescription = "Decrease"
					)
					Text("Delete", color = Color.White)
				}
				Button(
					onClick = {
						scope.launch {
							var imageUrl: String? = null
							if (imageBytes != null) {
								val name = List(10) { ('a'..'z').random() }.joinToString("")
								SupabaseClient.uploadImage("menu-images/$name.png", imageBytes!!)
								imageUrl = SupabaseClient.storage
									.from("images")
									.publicUrl("menu-images/$name.png")
							}

							val updatedBranchItem = branchItem?.copy(
								price = price,
								categoryId = selectedId,
								isFeatured = isFeatured == true,
								urlImg = imageUrl ?: imgUrl

							)
							if (updatedBranchItem != null) {
								BranchItem.update(itemId, updatedBranchItem)
								println(updatedBranchItem)
							}
						}
						navController?.popBackStack()
					},
					shape = RoundedCornerShape(8.dp),
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primary,
						contentColor = Color.White
					),
					modifier = Modifier.height(40.dp)
				) {
					Icon(
						Icons.Filled.Check,
						contentDescription = "Decrease"
					)
					Text("Update", color = Color.White)
				}
			}
		}
	}
}
