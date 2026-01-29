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
import containerised.pos.database.uploadImage
import containerised.pos.rememberImagePickerBytes
import containerised.pos.rememberImagePickerUri
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
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
	var expanded by remember { mutableStateOf(false) }
	LazyColumn{
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
					if (imageBytes == null) {
						Box(
							modifier = Modifier
								.size(120.dp)
								.clip(RoundedCornerShape(8.dp))
								.background(Color(0xFFACACAC)),
							contentAlignment = Alignment.Center
						) {}
					} else {
						Image(
							bitmap = imageBytes!!.decodeToImageBitmap(),
							contentDescription = null,
							modifier = Modifier
								.size(120.dp)
								.clip(RoundedCornerShape(8.dp))
						)
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
						value = "Input",
						onValueChange = {},
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
						var selected = "Placeholder"
						OutlinedTextField(
							label = { Text("Category") },
							value = selected,
							onValueChange = {},
							readOnly = true,
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
							listOf("Option 1", "Option 2", "Option 3").forEach {
								DropdownMenuItem(
									text = { Text(it) },
									onClick = {
										selected = it
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
							if (imageBytes == null) {
								return@launch
							} else {
								val name = List(10) { ('a'..'z').random() }.joinToString("")
								uploadImage("menu-images/$name.png", imageBytes!!)
							}
						}
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
