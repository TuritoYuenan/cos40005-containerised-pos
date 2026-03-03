package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import containerised.pos.models.BranchItem
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup

@Composable
fun MenuEditPage() {
    val branchId = "BRA26011700"
    //Promotion Fetching

    //Item Fetching
    var items by remember {mutableStateOf<Map<String, List<BranchItem>>>(emptyMap())}
    LaunchedEffect(Unit) {
        try {
            items = BranchItem.fetchByBranch(branchId)
                .sortedBy { it.itemId }
                .groupBy { it.categoryId ?: "Uncategorized" }
        } catch (e: Exception) {
            println("Error fetching data: ${e.message}")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        //Tab Set Up
        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Promotions", "Tags", "Items")

        Column {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            when (selectedTabIndex) {
                0 -> PromotionsTab()
                1 -> TagsTab()
                2 -> ItemsTab(items)
            }
        }
        //Button Set Up
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
                        onClick = {}
                    )
                    DropdownMenuItem(
                        text = { Text("New Tag") },
                        onClick = {}
                    )
                    DropdownMenuItem(
                        text = { Text("New Item") },
                        onClick = {}
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionsTab() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsTab() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsTab(
    groupedItems: Map<String, List<BranchItem>>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        groupedItems.forEach { (categoryId, branchItems) ->

            item {
                Text(
                    text = categoryId,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(
                items = branchItems,
                key = { it.itemId }
            ) { item ->
                ItemCard(item)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCard(
    item: BranchItem,
){
    Card(
        modifier = Modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    )   {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ){
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
            Box{
                var expanded by remember { mutableStateOf(false) }
                IconButton(onClick = {expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Item"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ){
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

