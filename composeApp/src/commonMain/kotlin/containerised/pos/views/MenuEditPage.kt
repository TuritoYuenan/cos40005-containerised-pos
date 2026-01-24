package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import containerised.pos.models.Item
import containerised.pos.models.fetchItem

@Preview
@Composable
fun MenuEditPage() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var fabMenuExpanded by remember { mutableStateOf(false) }
    val mockItems = listOf(
        Item(
            itemId = "ITEM001",
            itemName = "Classic Cheeseburger",
            itemDes = "Beef patty, cheddar cheese, lettuce, tomato",
            defaultPrice = 8.99f,
            defaultEstimatedPrep = "10 min"
        ),
        Item(
            itemId = "ITEM002",
            itemName = "Grilled Chicken Sandwich",
            itemDes = "Grilled chicken breast with garlic mayo",
            defaultPrice = 7.49f,
            defaultEstimatedPrep = "12 min"
        ),
        Item(
            itemId = "ITEM003",
            itemName = "Vegan Salad Bowl",
            itemDes = "Mixed greens, quinoa, avocado, tahini sauce",
            defaultPrice = 6.50f,
            defaultEstimatedPrep = "8 min"
        ),
        Item(
            itemId = "ITEM004",
            itemName = "French Fries",
            itemDes = null, // optional field works fine
            defaultPrice = 3.00f,
            defaultEstimatedPrep = "5 min"
        ),
        Item(
            itemId = "ITEM005",
            itemName = "Iced Latte",
            itemDes = "Espresso with milk and ice",
            defaultPrice = 4.25f,
            defaultEstimatedPrep = "3 min"
        )
    )

    val items = remember {mockItems}

    Scaffold(
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = { fabMenuExpanded = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }

                DropdownMenu(
                    expanded = fabMenuExpanded,
                    onDismissRequest = { fabMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Add Item") },
                        onClick = {
                            fabMenuExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Add Tag") },
                        onClick = {
                            fabMenuExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Add Promotion") },
                        onClick = {
                            fabMenuExpanded = false
                        }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Tabs
            SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
                listOf("Promotions", "Tags", "Items").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Content
            when (selectedTabIndex) {
                0 -> PromotionsTab()
                1 -> TagsTab()
                2 -> ItemsTab(items = items)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarSection(
    placeholderText: String
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    SearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = { active = false },
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text(placeholderText) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        modifier = Modifier.fillMaxWidth()
    ) {}
}

data class Promotion(
    val id: String,
    val title: String,
    val subtitle: String,
    val isSelected: Boolean = false
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionsTab() {
    val promotions = remember {
        listOf(
            Promotion("1", "Lorem Ipsum Title", "Lorem Ipsum Condition"),
            Promotion("2", "Black Friday", "20% off"),
            Promotion("3", "Member Sale", "Buy 1 Get 1", true)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBarSection("Search for Promotion")

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Promotions",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = promotions,
                key = { it.id } // VERY important for performance
            ) { promotion ->
                PromotionCard(
                    title = promotion.title,
                    subtitle = promotion.subtitle,
                    selected = promotion.isSelected
                )
            }
        }
    }
}

@Composable
fun PromotionCard(
    title: String,
    subtitle: String,
    selected: Boolean
) {
    val background =
        if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text("🖼")
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = {}) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
        }
    }
}

data class Tag(
    val id: String,
    val title: String,
    val isSelected: Boolean = false
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsTab() {
    val tags = remember {
        listOf(
            Tag("1", "Vegan"),
            Tag("2", "No Peanut"),
            Tag("3", "Dairy", isSelected = true)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBarSection("Search for Tag")

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Tags",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = tags,
                key = { it.id }
            ) { tag ->
                TagCard(
                    title = tag.title,
                    selected = tag.isSelected
                )
            }
        }
    }
}

@Composable
fun TagCard(
    title: String,
    selected: Boolean
) {
    val background =
        if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text("🖼")
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
            }

            IconButton(onClick = {}) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsTab(
    items: List<Item>
) {
    var searchResults by remember { mutableStateOf<List<Item>>(items) }

    Column(modifier = Modifier.fillMaxSize()) {

        SearchBarSection("Search for Items")

        ItemsContent(searchResults)
    }
}

@Composable
fun ItemsContent(items: List<Item>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {

        item {
            Text(
                "Items",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        items(items, key = { it.itemId }) { item ->
            ItemEditCard(
                item = item,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ItemEditCard(
    item: Item,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Image placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text("🖼")
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Item details
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
                    text = "$${item.defaultPrice}",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // ✅ Edit button
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Item"
                )
            }
        }
    }
}