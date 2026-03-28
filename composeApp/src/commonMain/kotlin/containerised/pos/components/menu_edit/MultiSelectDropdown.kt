package containerised.pos.components.menu_edit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectDropdown(
	label: String,
	items: List<Pair<String, String>>, // id to name
	selected: List<String>,
	onChange: (List<String>) -> Unit
) {
	var expanded by remember { mutableStateOf(false) }

	val selectedNames = items
		.filter { selected.contains(it.first) }
		.joinToString { it.second }

	ExposedDropdownMenuBox(
		expanded = expanded,
		onExpandedChange = { expanded = !expanded }
	) {
		OutlinedTextField(
			value = selectedNames,
			onValueChange = {},
			readOnly = true,
			modifier = Modifier
				.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
				.fillMaxWidth(),
			label = { Text(label) },
			placeholder = { Text("Select $label") },
			trailingIcon = {
				ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
			}
		)

		ExposedDropdownMenu(
			expanded = expanded,
			onDismissRequest = { expanded = false }
		) {
			items.forEach { (id, name) ->
				DropdownMenuItem(
					text = {
						Row(verticalAlignment = Alignment.CenterVertically) {
							Checkbox(
								checked = selected.contains(id),
								onCheckedChange = null
							)
							Spacer(Modifier.width(8.dp))
							Text(name)
						}
					},
					onClick = {
						val newList =
							if (selected.contains(id)) selected - id
							else selected + id

						onChange(newList)
					}
				)
			}
		}
	}
}
