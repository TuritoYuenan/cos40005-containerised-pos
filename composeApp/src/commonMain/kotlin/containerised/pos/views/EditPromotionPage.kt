package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPromotionPage(navController: NavController?) {
	Column{
		CenterAlignedTopAppBar(
			navigationIcon = {
				IconButton(onClick = { navController?.popBackStack() }) {
					Icon(
						imageVector = Icons.AutoMirrored.Filled.ArrowBack,
						contentDescription = "Back"
					)
				}
			},
			title = { Text("Promotion edit") }
		)
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 6.dp, horizontal = 12.dp),
		){
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 6.dp, horizontal = 12.dp),
				horizontalArrangement = Arrangement.spacedBy(60.dp)
			) {
				Box(
					modifier = Modifier
						.size(120.dp)
						.clip(RoundedCornerShape(8.dp))
						.background(Color(0xFFACACAC)),
					contentAlignment = Alignment.Center
				) {}
				Column(
					verticalArrangement = Arrangement.spacedBy(8.dp),
				) {
					Button(
						onClick = { /* TODO: Handle image upload */ },
						shape = RoundedCornerShape(50),
						colors = ButtonDefaults.buttonColors(
							containerColor = MaterialTheme.colorScheme.primary,
							contentColor = Color.White
						),
						modifier = Modifier.height(40.dp)
					) {
						Text("Upload", color = Color.White)
					}
					Text(
						text = "Supports PNG, JPEG, WEBP images below 5MB",
						color = Color.Black.copy(alpha = 0.5f),
						style = MaterialTheme.typography.labelSmall.copy(
							fontSize = 12.sp
						)
					)
				}

			}
		}
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 6.dp, horizontal = 12.dp),
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 6.dp, horizontal = 12.dp),
				verticalArrangement = Arrangement.spacedBy(6.dp)
			) {
				OutlinedTextField(
					value = "Input",
					onValueChange = {},
					label = { Text("Name") },
					singleLine = true,
					modifier = Modifier.fillMaxWidth(),
					trailingIcon = {
						Icon(
							imageVector = Icons.Outlined.Edit,
							contentDescription = "Action"
						)
					}
				)
				OutlinedTextField(
					value = "Input",
					onValueChange = {},
					label = { Text("Time") },
					singleLine = true,
					modifier = Modifier.fillMaxWidth(),
					trailingIcon = {
						Icon(
							imageVector = Icons.Outlined.EditCalendar,
							contentDescription = "Action"
						)
					}
				)
			}
		}
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 6.dp, horizontal = 12.dp),
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 6.dp, horizontal = 12.dp),
				verticalArrangement = Arrangement.spacedBy(6.dp)
			) {
				Text(
					text = "Condition",
					style = MaterialTheme.typography.titleSmall,
				)
				EditPromotionCondition()
				TextButton(
					modifier = Modifier
						.height(16.dp),
					contentPadding = PaddingValues(0.dp),
					onClick = {},
				){
					Text(
						text = "New Condition+",
						color = Color.Black.copy(alpha = 0.5f),
						style = MaterialTheme.typography.labelSmall.copy(
							fontSize = 12.sp
						)

					)
				}
			}
		}
		Row(
			horizontalArrangement = Arrangement.spacedBy(
				10.dp,
				Alignment.End
			),
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 6.dp, horizontal = 12.dp),
		){
			Button(
				onClick = {  },
				shape = RoundedCornerShape(8.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = Color.White
				),
				modifier = Modifier.height(40.dp)
			) {
				Icon(
					Icons.Filled.Delete,
					contentDescription = "Decrease"
				)
				Text("Delete", color = Color.White)
			}
			Button(
				onClick = {  },
				shape = RoundedCornerShape(8.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = Color.White
				),
				modifier = Modifier.height(40.dp)
			) {
				Icon(
					Icons.Filled.Check,
					contentDescription = "Decrease"
				)
				Text("Update", color = Color.White)
			}
		}
	}
}

@Composable
@Preview
fun EditPromotionCondition() {
	Row(
		modifier = Modifier
			.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	){
		Box(
			modifier = Modifier
				.height(25.dp)
				.width(55.dp)
				.background(MaterialTheme.colorScheme.primary),
		){
			Text(
				text = "Combo",
				modifier = Modifier.align(Alignment.Center),
				color = Color.White
			)
		}
		Spacer(modifier = Modifier.width(6.dp))
		Text(
			text = "Item 1, Item 2",
		)
		Spacer(modifier = Modifier.weight(1f))
		Box(
			modifier = Modifier
				.height(25.dp)
				.width(105.dp)
				.background(Color.LightGray),
		){
			Text(
				text = "Discount: 15%",
				modifier = Modifier.align(Alignment.Center)
			)
		}
	}
}
