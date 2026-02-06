package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import containerised.pos.CheckoutItemStorage
import containerised.pos.models.BranchItem
import containerised.pos.models.Order
import containerised.pos.models.OrderItem
import containerised.pos.models.fetchBranchItemByBranch
import containerised.pos.models.fetchBranchItemById
import containerised.pos.models.fetchOrderItemByOrder
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun KitchenDisplayPage() {
	Column{

	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun KitchenDisplayOrderItem(order: Order){
	var orderItems by remember { mutableStateOf<List<OrderItem>>(emptyList()) }
	var menuItems by remember { mutableStateOf<List<BranchItem?>>(emptyList()) }
	var itemMap by remember { mutableStateOf<Map<String, List<BranchItem?>>>(emptyMap()) }
	var error by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(Unit) {
		try {
			orderItems = fetchOrderItemByOrder(order.orderId)
			println("Fetched ${orderItems.size} order items:")
			orderItems.forEach { item ->
				println(
					"• ${item.itemId}: ${item.quantity} (${item.subtotal})"
				)
				menuItems = menuItems + fetchBranchItemById(item.itemId)
			}
			itemMap = menuItems.groupBy { (it?.categoryId ?: "catid") }

		} catch (e: Exception) {
			error = e.message
			println("Error: $error")
		}
	}
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(8.dp))
	){
		Column {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.background(MaterialTheme.colorScheme.primary)
			) {
				Box{}
				Column {
					Text(
						text = "Order #57",
						style = MaterialTheme.typography.titleMedium,
						color = Color.White,
					)
					Text(
						text = "Table No. 07",
						color = Color.White,
					)
				}
			}
			Column {
				Text(
					text = "Category",
					style = MaterialTheme.typography.titleMedium,
				)
				Text(
					text = "1 x Item"
				)
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
					onClick = { },
					shape = RoundedCornerShape(16.dp),
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.outlineVariant,
						contentColor = Color.Black
					),
					modifier = Modifier.height(40.dp)
				) {
					Icon(
						Icons.Filled.Close,
						contentDescription = "Cancel"
					)
					Text("Cancel", color = Color.Black)
				}
				Button(
					onClick = { },
					shape = RoundedCornerShape(16.dp),
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primary,
						contentColor = Color.White
					),
					modifier = Modifier.height(40.dp)
				) {
					Icon(
						Icons.Filled.Check,
						contentDescription = "Done"
					)
					Text("Done", color = Color.White)
				}
			}
		}
	}
}
