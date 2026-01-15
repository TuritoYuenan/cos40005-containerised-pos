package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.CheckoutDiscountItem
import containerised.pos.components.CheckoutMenuItem
import containerised.pos.models.MenuItem
import org.jetbrains.compose.ui.tooling.preview.Preview

val mockCheckoutItems = listOf(
	MenuItem(
		id = "1",
		name = "Margherita Pizza",
		description = "Classic pizza with tomato sauce, mozzarella, and basil.",
		price = 8.99f,
		categoryID = "cat1",
		isAvailable = true,
		estimatedPreparationTime = 15,
		imageURL = null,
		branchID = "branch1",
		specialNotes = null,
	),
	MenuItem(
		id = "2",
		name = "Caesar Salad",
		description = "Crisp romaine lettuce with Caesar dressing, croutons, and Parmesan cheese.",
		price = 6.49f,
		categoryID = "cat2",
		isAvailable = true,
		estimatedPreparationTime = 10,
		imageURL = null,
		branchID = "branch1",
		specialNotes = null,
	),
	MenuItem(
		id = "3",
		name = "Spaghetti Carbonara",
		description = "Spaghetti pasta with creamy sauce, pancetta, and Parmesan cheese.",
		price = 10.99f,
		categoryID = "cat1",
		isAvailable = false,
		estimatedPreparationTime = 20,
		imageURL = null,
		branchID = "branch1",
		specialNotes = null,
	),
)
@Composable
@Preview
fun CheckOutWebPage(navController: NavController?) {
	LazyColumn(
		modifier = Modifier
			.background(MaterialTheme.colorScheme.surface)
	) {
		item {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(40.dp)
					.background(MaterialTheme.colorScheme.surface),
				contentAlignment = Alignment.Center
			) {
				Text(
					text = "My Cart",
					style = MaterialTheme.typography.titleMedium,
					color = Color.Black
				)
			}
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 6.dp, horizontal = 12.dp),
				shape = RoundedCornerShape(12.dp),
				elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
			) {
				Column(
					modifier = Modifier
						.background(Color.White)
				) {
					Row(
						modifier = Modifier
							.padding(vertical = 6.dp),
					) {
						Icon(
							Icons.Filled.RoomService,
							contentDescription = "Decrease"
						)
						Text(
							text = "Table 1's order",
							style = MaterialTheme.typography.titleMedium,
						)
					}
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 3.dp, horizontal = 12.dp),
						verticalArrangement = Arrangement.spacedBy(6.dp)
					) {
						mockCheckoutItems.forEach { item -> CheckoutMenuItem(item) }
					}
				}
			}
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 6.dp, horizontal = 12.dp),
				shape = RoundedCornerShape(12.dp),
				elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
			) {
				Column(
					modifier = Modifier
						.background(Color.White)
				) {
					Row(
						modifier = Modifier
							.padding(vertical = 6.dp),
					) {
						Icon(
							Icons.Filled.Discount,
							contentDescription = "Decrease"
						)
						Text(
							text = "Table 1's discount",
							style = MaterialTheme.typography.titleMedium,
						)
					}
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 3.dp, horizontal = 12.dp),
						verticalArrangement = Arrangement.spacedBy(6.dp)
					) {
						CheckoutDiscountItem()
						CheckoutDiscountItem()
					}
				}
			}
			Box {
				Card(
					modifier = Modifier
						.fillMaxWidth()
						.background(MaterialTheme.colorScheme.surface)
						.padding(vertical = 6.dp, horizontal = 6.dp)
						.align(Alignment.BottomCenter),
				) {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.clip(RoundedCornerShape(2.dp))
							.background(MaterialTheme.colorScheme.primaryContainer)
							.padding(vertical = 6.dp, horizontal = 6.dp),

						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						Text("Order:")
						Text("$300")
					}
				}
			}
		}
	}
}
