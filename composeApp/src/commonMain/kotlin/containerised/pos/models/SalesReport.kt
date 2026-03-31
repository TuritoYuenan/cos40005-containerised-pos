package containerised.pos.models

import containerised.pos.database.SupabaseClient
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class SalesReport(
	@SerialName("id") val id: String,
	@SerialName("granularity") val granularity: Granularity,
	@SerialName("period_start") val periodStart: LocalDate,
	@SerialName("period_end") val periodEnd: LocalDate,
	@SerialName("currency") val currency: String = "USD",
	@SerialName("gross_sales") val grossSales: Double = 0.0,
	@SerialName("discount_total") val discountTotal: Double = 0.0,
	@SerialName("net_sales") val netSales: Double = 0.0,
	@SerialName("orders_count") val ordersCount: Long = 0,
	@SerialName("source_job") val sourceJob: String? = null,
	@SerialName("generated_at") val generatedAt: Instant = Instant.DISTANT_PAST
) {
	@Serializable
	enum class Granularity {
		DAILY,
		WEEKLY,
		MONTHLY,
		YEARLY
	}

	companion object {
		suspend fun fetchAll(): List<SalesReport> = SupabaseClient.db["sales_reports"]
			.select()
			.decodeList<SalesReport>()

		suspend fun fetchByID(id: String): SalesReport =
			SupabaseClient.db["sales_reports"]
				.select { filter { eq("id", id) } }
				.decodeSingle<SalesReport>()

		val MOCK = SalesReport(
			id = "report_123",
			granularity = Granularity.DAILY,
			periodStart = LocalDate(2024, 6, 1),
			periodEnd = LocalDate(2024, 6, 1),
			currency = "USD",
			grossSales = 12345.67,
			discountTotal = 234.50,
			netSales = 12111.17,
			ordersCount = 89,
			sourceJob = null,
			generatedAt = Instant.DISTANT_PAST
		)
	}
}
