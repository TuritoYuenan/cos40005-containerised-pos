package containerised.pos.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.CreateButton
import containerised.pos.components.DeleteButton
import containerised.pos.components.UpdateButton
import containerised.pos.models.Tag
import containerised.pos.routes.StaffRoutes
import kotlinx.coroutines.launch

private data class EditTagFormState(
	var name: String = "",
	var description: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTagPage(navController: NavController?, tagId: String? = null) {
	val scope = rememberCoroutineScope()
	var formState by remember { mutableStateOf(EditTagFormState()) }
	var tag by remember { mutableStateOf<Tag?>(null) }

	LaunchedEffect(Unit) {
		try {
			if (tagId != null) {
				tag = Tag.fetchById(tagId)
			}
			println(tagId)

			tag?.let {
				formState = EditTagFormState(
					name = it.tagName,
					description = it.tagDes ?: ""
				)
			}

		} catch (e: Exception) {
			println("Error fetching tag: ${e.message}")
		}
	}

	LazyColumn {
		item {
			TagFormSection(
				formState = formState,
				onFormChange = { formState = it }
			)
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(12.dp),
				horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
			) {
				if (tagId == null) {

					CreateButton() {
						scope.launch {
							Tag.create(
								name = formState.name,
								description = formState.description
							)
							navController?.navigate(StaffRoutes.MenuEdit)
						}
					}
				} else {
					DeleteButton {
						scope.launch {
							Tag.deleteById(tagId)
							navController?.navigate(StaffRoutes.MenuEdit)
						}
					}
					UpdateButton {
						scope.launch {
							Tag.updateById(
								id = tagId,
								name = formState.name,
								description = formState.description
							)
							navController?.navigate(StaffRoutes.MenuEdit)
						}
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagFormSection(
	formState: EditTagFormState,
	onFormChange: (EditTagFormState) -> Unit
) = Card(Modifier.fillMaxWidth().padding(12.dp, 6.dp)) {
	Column(
		Modifier.fillMaxWidth().padding(12.dp),
		Arrangement.spacedBy(10.dp)
	) {
		// Tag Name
		OutlinedTextField(
			value = formState.name,
			onValueChange = { onFormChange(formState.copy(name = it)) },
			label = { Text("Tag Name") },
			modifier = Modifier.fillMaxWidth(),
			trailingIcon = {
				Icon(Icons.Default.Edit, "Edit Tag Name")
			}
		)

		// Tag Description
		OutlinedTextField(
			value = formState.description ?: "",
			onValueChange = {
				onFormChange(formState.copy(description = it))
			},
			label = { Text("Description") },
			modifier = Modifier.fillMaxWidth(),
			trailingIcon = {
				Icon(Icons.Default.Edit, "Edit Tag Description")
			},
			minLines = 2
		)
	}
}
