package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import containerised.pos.models.BranchItem
import containerised.pos.models.fetchAndJoinBranchItemByBranch
import containerised.pos.models.fetchBranchItemById
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun MenuEditPage() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var fabMenuExpanded by remember { mutableStateOf(false) }

    var items by remember {mutableStateOf<List<BranchItem>>(emptyList())}
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            items = fetchAndJoinBranchItemByBranch("BRA26011700")
        } catch (e: Exception) {
            println("Error fetching data: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    val groupedItems = items.groupBy { it.category }

    Column(modifier = Modifier.padding(16.dp)) {

        if (isLoading) {
            Text("Loading...")
            return@Column
        }

        groupedItems.forEach { (category, branchItems) ->

            // Category title
            Text(
                text = category?.categoryName ?: "Uncategorized",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Items under category
            branchItems.forEach { branchItem ->
                Text(
                    text = "• ${branchItem.itemName ?: "Unknown item"}",
                    modifier = Modifier.padding(start = 12.dp)
                )
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
    items: List<BranchItem>
) {
    val groupedItems = remember(items) {
        items.groupBy { it.categoryId ?: "Uncategorized" }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBarSection("Search for Items")
        GroupedItemsContent(groupedItems)
    }
}

@Composable
fun GroupedItemsContent(
    groupedItems: Map<String, List<BranchItem>>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        groupedItems.forEach { (categoryid, branchItems) ->
            item {
                Text(
                    text = categoryid,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            // Items under category
            items(
                items = branchItems,
                key = { it.itemId }
            ) { branchItem ->
                ItemEditCard(
                    branchItem = branchItem,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ItemEditCard(
    branchItem: BranchItem,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {}
) {
    val item by produceState<BranchItem?>(initialValue = null, branchItem.itemId) {
        value = fetchBranchItemById(branchItem.itemId)
    }

    when (val currentItem = item) {
        null -> {
            CircularProgressIndicator()
        }
        else -> {
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

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = currentItem.itemName,
                            style = MaterialTheme.typography.titleSmall
                        )

                        currentItem.itemDes?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$${currentItem.price}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Item"
                        )
                    }
                }
            }
        }
    }
}
