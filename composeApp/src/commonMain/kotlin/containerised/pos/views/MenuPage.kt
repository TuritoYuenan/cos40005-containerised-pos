package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import containerised.pos.models.MenuItem
import containerised.pos.models.deleteMenuItem
import containerised.pos.models.fetchMenuItem
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun MenuPage(navController: NavController) {
	MaterialTheme {
		Column {
			MenuTopBar()
			MenuList(navController)
		}
	}
}

@Composable
fun MenuTopBar() {
	var searchText by remember { mutableStateOf("") }

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.primaryContainer)
			.padding(horizontal = 8.dp, vertical = 6.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		// Left: Menu button
		IconButton(onClick = { println("Menu clicked") }) {
			Icon(
				imageVector = Icons.Default.Menu,
				contentDescription = "Menu",
				tint = MaterialTheme.colorScheme.onPrimaryContainer
			)
		}

		// Center: Search bar
		Box(
			modifier = Modifier
				.weight(1f)
				.height(40.dp)
				.background(Color.White, shape = CircleShape)
				.padding(horizontal = 12.dp),
			contentAlignment = Alignment.CenterStart
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(
					imageVector = Icons.Default.Search,
					contentDescription = "Search",
					tint = Color.Gray
				)
				if (searchText.isEmpty()) {
					Spacer(Modifier.width(8.dp))
					Text("Search menu item", color = Color.Gray, fontSize = 14.sp)
				}
			}

			BasicTextField(
				value = searchText,
				onValueChange = { newText: String -> searchText = newText },
				singleLine = true,
				textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
				modifier = Modifier
					.fillMaxWidth()
					.align(Alignment.CenterStart)
					.padding(start = 32.dp)
			)
		}

		// Right: Profile button
		IconButton(onClick = { println("Profile clicked") }) {
			Icon(
				imageVector = Icons.Default.Person,
				contentDescription = "Profile",
				tint = MaterialTheme.colorScheme.onPrimaryContainer
			)
		}
	}
}

@Composable
@Preview
fun MenuItemCard(
	itemName: String,
	price: String,
	imageUrl: String?,
	onEdit: () -> Unit,
	onDelete: () -> Unit
) {
	var expanded by remember { mutableStateOf(false) }
	var showDeleteDialog by remember { mutableStateOf(false) }

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 6.dp, horizontal = 12.dp),
		shape = RoundedCornerShape(12.dp),
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.surface)
				.padding(12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			// Left: Image
			if (!imageUrl.isNullOrBlank()) {
				KamelImage(
					resource = asyncPainterResource(imageUrl),
					contentDescription = itemName,
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.size(64.dp)
						.clip(RoundedCornerShape(8.dp)),
					onFailure = {
						// fallback if the image fails to load
						Box(
							modifier = Modifier
								.size(64.dp)
								.clip(RoundedCornerShape(8.dp))
								.background(Color(0xFF0358AD)),
							contentAlignment = Alignment.Center
						) {}
					}
				)
			} else {
				// Blue placeholder if no image
				Box(
					modifier = Modifier
						.size(64.dp)
						.clip(RoundedCornerShape(8.dp))
						.background(Color(0xFF0358AD)),
					contentAlignment = Alignment.Center
				) {}
			}

			Spacer(modifier = Modifier.width(12.dp))

			// Middle: Name and Price
			Column(
				modifier = Modifier.weight(1f)
			) {
				Text(
					text = itemName,
					style = MaterialTheme.typography.titleSmall.copy(
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.onSurface
					)
				)
				Spacer(modifier = Modifier.height(4.dp))
				Text(
					text = price,
					style = MaterialTheme.typography.bodyMedium.copy(
						fontSize = 14.sp
					)
				)
			}

			// Right: Dropdown Menu (⋮)
			Box {
				IconButton(onClick = { expanded = true }) {
					Icon(
						imageVector = Icons.Default.MoreVert,
						contentDescription = "Options"
					)
				}

				DropdownMenu(
					expanded = expanded,
					onDismissRequest = { expanded = false },
				) {
					DropdownMenuItem(
						text = { Text("Edit") },
						onClick = {
							expanded = false
							onEdit()
						}
					)
					DropdownMenuItem(
						text = { Text("Delete") },
						onClick = {
							expanded = false
							showDeleteDialog = true
						}
					)
				}
			}
			if (showDeleteDialog) {
				AlertDialog(
					onDismissRequest = { showDeleteDialog = false },
					title = { Text("Delete Item") },
					text = { Text("Are you sure you want to delete this item?") },
					confirmButton = {
						TextButton(onClick = {
							showDeleteDialog = false
							onDelete()
						}) {
							Text("Yes")
						}
					},
					dismissButton = {
						TextButton(onClick = { showDeleteDialog = false }) {
							Text("Cancel")
						}
					}
				)
			}
		}
	}
}

@Composable
fun MenuList(navController: NavController) {
	val scope = rememberCoroutineScope()
	var menuItems by remember { mutableStateOf<List<MenuItem>?>(null) }
	var error by remember { mutableStateOf<String?>(null) }
	LaunchedEffect(Unit) {
		try {
			// Fetch all
			menuItems = fetchMenuItem()
			println("Fetched ${menuItems!!.size} menu items:")
			menuItems!!.forEach { item ->
				println(
					"• ${item.id}: ${item.name} (${item.price})"
				)
			}
		} catch (e: Exception) {
			error = e.message
			println("Error: $error")
		}
	}
	LazyColumn(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 8.dp, vertical = 6.dp)
	){
		item {
			Text(
				text = "All items",
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 4.dp, horizontal = 12.dp),
				style = MaterialTheme.typography.titleMedium.copy(
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.onSurface
				),
				textAlign = TextAlign.Start
			)
		}
		menuItems?.let { list ->
			items(list) { item ->
				MenuItemCard(
					itemName = item.name,
					price = item.price.toString(),
					imageUrl = item.imageURL,
					onEdit = {
						navController.navigate("edit/${item.id}")
						println("itemId passed to EditMenuUI = ${item.id}")
					},
					onDelete = {
						scope.launch {
							deleteMenuItem(item.id)
							menuItems = fetchMenuItem()
						}
					}
				)
			}
		}
	}
}
