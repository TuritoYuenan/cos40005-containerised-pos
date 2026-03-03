@file:OptIn(ExperimentalMaterial3Api::class)

package containerised.pos.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import posapplication.composeapp.generated.resources.Res
import posapplication.composeapp.generated.resources.app_name

@Composable
fun StaffTopBar(currentRoute: String?) {
	when (currentRoute) {
		"inventory" -> InventoryTopBar()
		else -> GenericStaffTopBar()
	}
}

@Composable
private fun GenericStaffTopBar() {
	CenterAlignedTopAppBar(
		title = { Text(stringResource(Res.string.app_name)) }
	)
}

@Composable
private fun InventoryTopBar() {
	CenterAlignedTopAppBar(
		title = { Text("Inventory Management") }
	)
}
