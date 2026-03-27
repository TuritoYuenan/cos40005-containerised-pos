package containerised.pos.models

import kotlinx.serialization.Serializable

@Serializable
data class SalesReport(
	val id: String,
	val date: String,
	val totalSales: Double,
	val totalOrders: Int,
	val topSellingItems: List<String>
) {
	companion object {
		val MOCK = SalesReport(
			id = "report_123",
			date = "2024-06-01",
			totalSales = 12345.67,
			totalOrders = 89,
			topSellingItems = listOf("Item A", "Item B", "Item C")
		)
	}
}
