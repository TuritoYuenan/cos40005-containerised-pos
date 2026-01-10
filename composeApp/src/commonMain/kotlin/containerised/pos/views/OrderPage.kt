package containerised.pos.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import containerised.pos.components.OrderPageMenuItem
import containerised.pos.models.MenuItem
import containerised.pos.models.fetchMenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderPage(paddingValues: PaddingValues) {
	var menuItems by remember { mutableStateOf<List<MenuItem>>(listOf()) }
	LaunchedEffect(Unit) {
		withContext(Dispatchers.Default) {
			menuItems = fetchMenuItem()
		}
	}

	Column(modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState())) {
		Text("Drinks", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(8.dp))
		LazyRow(Modifier.padding(8.dp)) { items(menuItems) { item -> OrderPageMenuItem(item) } }

		Text("Drinks", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(8.dp))
		LazyRow(Modifier.padding(8.dp)) { items(menuItems) { item -> OrderPageMenuItem(item) } }
	}
}

@Preview
@Composable
fun OrderPagePreview() {
	MaterialTheme {
		OrderPage(PaddingValues())
	}
}
