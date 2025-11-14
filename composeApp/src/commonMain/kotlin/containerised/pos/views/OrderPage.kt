package containerised.pos.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import containerised.pos.OrderPageMenuItem
import containerised.pos.models.MenuItem
import containerised.pos.models.fetchMenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import posapplication.composeapp.generated.resources.Res
import posapplication.composeapp.generated.resources.app_name
import posapplication.composeapp.generated.resources.baseline_menu_24

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun OrderPage() {
	var menuItems by remember { mutableStateOf<List<MenuItem>>(listOf()) }
	LaunchedEffect(Unit) {
		withContext(Dispatchers.Default) {
			menuItems = fetchMenuItem()
		}
	}
	MaterialTheme {
		Scaffold(
			topBar = {
				CenterAlignedTopAppBar(
					title = { Text(stringResource(Res.string.app_name)) },
					navigationIcon = {
						IconButton(onClick = {}) {
							Icon(
								imageVector = vectorResource(Res.drawable.baseline_menu_24),
								contentDescription = "Menu",
								modifier = Modifier,
								tint = MaterialTheme.colorScheme.onSurface
							)
						}
					},
					actions = {
						IconButton(onClick = {}) {
//							Icon(imageVector = vectorResource(Res.drawable.compose_multiplatform))
						}
					},
				)
			},
			contentWindowInsets = ScaffoldDefaults.contentWindowInsets
		) { paddingValues ->
			Column(modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState())) {
				Text("Drinks", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(8.dp))
				LazyRow(Modifier.padding(8.dp)) { items(menuItems) { item -> OrderPageMenuItem(item) } }

				Text("Drinks", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(8.dp))
				LazyRow(Modifier.padding(8.dp)) { items(menuItems) { item -> OrderPageMenuItem(item) } }
			}
		}
	}
}
