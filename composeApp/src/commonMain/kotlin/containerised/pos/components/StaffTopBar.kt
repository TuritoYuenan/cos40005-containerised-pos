package containerised.pos.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import posapplication.composeapp.generated.resources.Res
import posapplication.composeapp.generated.resources.app_name

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffTopBar() {
	CenterAlignedTopAppBar(
		title = { Text(stringResource(Res.string.app_name)) },
		navigationIcon = {
			IconButton(onClick = {}) {
				Icon(
					imageVector = Icons.Filled.Menu,
					contentDescription = "Menu",
					modifier = Modifier,
					tint = MaterialTheme.colorScheme.onSurface
				)
			}
		},
		actions = {
			IconButton(onClick = {}) {
//				Icon(imageVector = vectorResource(Res.drawable.compose_multiplatform))
			}
		},
	)
}
