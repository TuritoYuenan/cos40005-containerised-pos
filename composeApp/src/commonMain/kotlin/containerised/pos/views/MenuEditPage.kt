package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.database.SupabaseClient
import containerised.pos.models.BranchItem
import containerised.pos.models.Category
import containerised.pos.models.Promotion
import containerised.pos.models.Tag
import containerised.pos.models.User.Companion.fetchBranchById
import containerised.pos.routes.StaffRoutes
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun MenuEditPage(navController: NavController) {
	var items by remember { mutableStateOf<Map<String, List<BranchItem>>>(emptyMap()) }
	var promotions by remember { mutableStateOf<List<Promotion>>(emptyList()) }
	var tags by remember { mutableStateOf<List<Tag>>(emptyList()) }
    var categories by remember { mutableStateOf<List<Category>>(emptyList())}
	val userId = SupabaseClient.auth.currentUserOrNull()?.id
	var branchId by remember { mutableStateOf<String?>(null) }


	LaunchedEffect(Unit) {
		try {
			branchId = fetchBranchById(userId?: "")
            categories= Category.fetchAll()
            val categoryMap = categories.associate { it.categoryId to it.categoryName }
			items = BranchItem.fetchByBranch(branchId!!)
				.sortedBy { it.itemId }
				.groupBy { item -> categoryMap[item.categoryId] ?: "Uncategorized" }
			promotions = Promotion.fetchByBranch(branchId!!)
			tags = Tag.fetchAll()
		} catch (e: Exception) {
			println("Error fetching data: ${e.message}")
		}
	}

	Box(modifier = Modifier.fillMaxSize()) {
		//Tab Set Up
		var selectedTabIndex by remember { mutableStateOf(0) }
		val tabs = listOf("Promotions", "Tags", "Items")

		Column {
			PrimaryTabRow(selectedTabIndex) {
				tabs.forEachIndexed { index, title ->
					Tab(
						selected = selectedTabIndex == index,
						onClick = { selectedTabIndex = index },
						text = { Text(title) }
					)
				}
			}
			when (selectedTabIndex) {
				0 -> PromotionsTab(
					promotions = promotions,
					onPromotionEdit = { promotion ->
						navController.navigate(
							StaffRoutes.EditPromotion(promotionId = promotion.promotionId)
						)
					},
				)

				1 -> TagsTab(
					tags = tags,
					onTagEdit = { tag ->
						navController.navigate(
							StaffRoutes.EditTag(tagId = tag.tagId)
						)
					}
				)

				2 -> ItemsTab(
					groupedItems = items,
					onEditItem = { item ->
						navController.navigate(
							StaffRoutes.EditItem(itemId = item.itemId)
						)
					}
				)
			}
		}

		var expanded by remember { mutableStateOf(false) }
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp),
			contentAlignment = Alignment.BottomEnd
		) {
			Box {
				FloatingActionButton(
					onClick = { expanded = !expanded }
				) {
					Icon(Icons.Filled.Add, contentDescription = "Add")
				}

				DropdownMenu(
					expanded = expanded,
					onDismissRequest = { expanded = false }
				) {
					DropdownMenuItem(
						text = { Text("New Promotion") },
						onClick = {
							navController.navigate(
								StaffRoutes.EditPromotion(promotionId = null)
							)
						}
					)
					DropdownMenuItem(
						text = { Text("New Tag") },
						onClick = {
							navController.navigate(
								StaffRoutes.EditTag(tagId = null)
							)
						}
					)
					DropdownMenuItem(
						text = { Text("New Item") },
						onClick = {
							navController.navigate(
								StaffRoutes.EditItem(itemId = null)
							)
						}
					)
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionsTab(
	promotions: List<Promotion>,
	onPromotionEdit: (promotion: Promotion) -> Unit,
) {
	Box(
		Modifier
			.fillMaxWidth()
			.padding(16.dp)
			.clip(RoundedCornerShape(10.dp))
			.background(MaterialTheme.colorScheme.surfaceContainerLowest)
	)
	{
		Text(text = "Promotions")
		LazyColumn(
			Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
            items(promotions) { promotion ->
                PromotionRow(
                    promotion = promotion,
                    onEdit = { onPromotionEdit(promotion) }
                )
            }
		}
	}
}

@Composable
fun PromotionRow(
    promotion: Promotion,
    onEdit: () -> Unit
) {
    val backgroundColor = if (promotion.isActive)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            // 🔹 Banner (FULL width again)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val imageUrl = promotion.urlImg

                if (!imageUrl.isNullOrBlank()) {
                    KamelImage(
                        resource = asyncPainterResource(imageUrl),
                        contentDescription = "Promotion Image",
                        modifier = Modifier.fillMaxSize(),
                        onLoading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        },
                        onFailure = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("❌ Failed to load")
                            }
                        }
                    )
                } else {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🖼 No Image")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // 🔹 Info + Edit row (like your original)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Start: ${promotion.startDate ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "End: ${promotion.endDate ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Promotion"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsTab(
	tags: List<Tag>,
	onTagEdit: (Tag) -> Unit,
) {
	Column(
		Modifier
			.fillMaxWidth()
			.padding(16.dp)
	) {

		Text(
			text = "Tags",
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.padding(bottom = 12.dp)
		)

		Card(
			shape = RoundedCornerShape(12.dp),
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
			)
		) {

			LazyColumn(
				modifier = Modifier.fillMaxWidth(),
				contentPadding = PaddingValues(vertical = 8.dp)
			) {

				items(tags) { tag ->

					TagRow(tag, onTagEdit)

				}
			}
		}
	}
}

@Composable
fun TagRow(
	tag: Tag,
	onTagEdit: (Tag) -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 12.dp, vertical = 6.dp),
		shape = RoundedCornerShape(10.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.primaryContainer
		)
	) {

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(14.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(
				modifier = Modifier.weight(1f)
			) {
				//Name Row
				Text(
					text = tag.tagName,
					style = MaterialTheme.typography.titleSmall
				)
				//Description Row
				val description = tag.tagDes ?: "There's no description for this."
				Spacer(Modifier.height(2.dp))
				Text(
					text = description,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
			//Button to Edit
			IconButton(
				onClick = { onTagEdit(tag) }
			) {
				Icon(
					imageVector = Icons.Filled.Edit,
					contentDescription = "Edit Tag"
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsTab(
	groupedItems: Map<String, List<BranchItem>>,
	onEditItem: (BranchItem) -> Unit
) {
	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.spacedBy(12.dp),
		contentPadding = PaddingValues(16.dp)
	) {
		groupedItems.forEach { (category, branchItems) ->

			item {
				Text(
					text = category,
					style = MaterialTheme.typography.titleMedium,
					modifier = Modifier.padding(vertical = 8.dp)
				)
			}

			items(
				items = branchItems,
				key = { it.itemId }
			) { item ->
				ItemCard(
					item = item,
					onEdit = { onEditItem(item) }
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCard(
	item: BranchItem,
	onEdit: () -> Unit
) {
    val imageUrl = item.urlImg

	Card(
		modifier = Modifier,
		shape = RoundedCornerShape(8.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant,
		)
	) {
		Row(
			modifier = Modifier.fillMaxWidth().padding(12.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    KamelImage(
                        resource = asyncPainterResource(imageUrl),
                        contentDescription = "Item Image",
                        modifier = Modifier.fillMaxSize(),
                        onLoading = {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(strokeWidth = 2.dp)
                            }
                        },
                        onFailure = {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("❌")
                            }
                        }
                    )
                } else {
                    Text("🖼")
                }
            }

			Spacer(modifier = Modifier.width(12.dp))

			Column(
				modifier = Modifier.weight(1f)
			) {
				Text(
					text = item.itemName,
					style = MaterialTheme.typography.titleSmall
				)

				item.itemDes?.let {
					Text(
						text = it,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}

				Spacer(modifier = Modifier.height(4.dp))

				Text(
					text = "$${item.price}",
					style = MaterialTheme.typography.labelMedium
				)
			}
			Box {
				var expanded by remember { mutableStateOf(false) }
				IconButton(onClick = onEdit) {
					Icon(
						imageVector = Icons.Default.Edit,
						contentDescription = "Edit Item"
					)
				}
				DropdownMenu(
					expanded = expanded,
					onDismissRequest = { expanded = false }
				) {
					DropdownMenuItem(
						text = { Text("Edit") },
						onClick = {}
					)
					DropdownMenuItem(
						text = { Text("Delete") },
						onClick = {}
					)
				}
			}
		}
	}
}

@Composable
fun Test()
{

}
