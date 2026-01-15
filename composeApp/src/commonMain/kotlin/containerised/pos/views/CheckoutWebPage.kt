package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun CheckOutWebPage(navController: NavController?) {
	LazyColumn {
		item {
			CenterAlignedTopAppBar(
				navigationIcon = {
					IconButton(onClick = { navController?.popBackStack() }) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Back"
						)
					}
				},
				title = { Text("My Cart") }
			)
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
						CheckoutMenuItem("Lorem ipsum", 100f)
						CheckoutMenuItem("Lorem ipsum", 100f)
						CheckoutMenuItem("Lorem ipsum", 100f)
						CheckoutMenuItem("Lorem ipsum", 100f)
						CheckoutMenuItem("Lorem ipsum", 100f)
						CheckoutMenuItem("Lorem ipsum", 100f)
						CheckoutMenuItem("Lorem ipsum", 100f)
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
