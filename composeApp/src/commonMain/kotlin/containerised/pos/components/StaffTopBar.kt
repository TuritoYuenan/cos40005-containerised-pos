package containerised.pos.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import posapplication.composeapp.generated.resources.Res
import posapplication.composeapp.generated.resources.app_name
import posapplication.composeapp.generated.resources.baseline_menu_24

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffTopBar() {
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
//				Icon(imageVector = vectorResource(Res.drawable.compose_multiplatform))
			}
		},
	)
}
