package containerised.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun MenuUI() {
	MaterialTheme {
		Column {
			MenuTopBar()

		}
	}
}
@Composable
fun MenuTopBar() {
	var searchText by remember { mutableStateOf("") }

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.primaryContainer)
			.padding(horizontal = 8.dp, vertical = 6.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		// Left: Menu button
		IconButton(onClick = { println("Menu clicked") }) {
			Icon(
				imageVector = Icons.Default.Menu,
				contentDescription = "Menu",
				tint = MaterialTheme.colorScheme.onPrimaryContainer
			)
		}

		// Center: Search bar
		Box(
			modifier = Modifier
				.weight(1f)
				.height(40.dp)
				.background(Color.White, shape = CircleShape)
				.padding(horizontal = 12.dp),
			contentAlignment = Alignment.CenterStart
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(
					imageVector = Icons.Default.Search,
					contentDescription = "Search",
					tint = Color.Gray
				)
				if (searchText.isEmpty()) {
					Spacer(Modifier.width(8.dp))
					Text("Search menu item", color = Color.Gray, fontSize = 14.sp)
				}
			}

			BasicTextField(
				value = searchText,
				onValueChange = { newText: String -> searchText = newText },
				singleLine = true,
				textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
				modifier = Modifier
					.fillMaxWidth()
					.align(Alignment.CenterStart)
					.padding(start = 32.dp)
			)
		}

		// Right: Profile button
		IconButton(onClick = { println("Profile clicked") }) {
			Icon(
				imageVector = Icons.Default.Person,
				contentDescription = "Profile",
				tint = MaterialTheme.colorScheme.onPrimaryContainer
			)
		}
	}
}
