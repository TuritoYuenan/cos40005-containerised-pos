package containerised.pos.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import containerised.pos.models.*
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun List<BranchItem>.ImageSlider(
	modifier: Modifier = Modifier.widthIn(0.dp, 512.dp).aspectRatio(2f)
) = Card(modifier) {
	if (isEmpty()) return@Card Box(
		Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
		Alignment.Center,
	) {
		Text(
			"No items available",
			color = MaterialTheme.colorScheme.onPrimary,
			style = MaterialTheme.typography.titleMedium
		)
	}

	HorizontalPager(
		rememberPagerState(pageCount = { size }),
		Modifier.fillMaxSize()
	) { page ->
		KamelImage(
			{
				asyncPainterResource(
					this@ImageSlider[page].urlImg ?: "https://placehold.co/512x256"
				)
			},
			this@ImageSlider[page % size].itemName,
			Modifier.fillMaxSize(),
			contentScale = ContentScale.Crop,
			onFailure = {
				Box(
					Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
					Alignment.Center,
				) {
					Text(
						this@ImageSlider[page].itemName,
						color = MaterialTheme.colorScheme.onPrimary,
						style = MaterialTheme.typography.titleMedium
					)
				}
			}
		)
	}
}

@Composable
fun List<Tag>.Row(
	modifier: Modifier = Modifier,
	onFilter: (String) -> Unit = { },
) = LazyRow(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
	items(size) { index ->
		FilterChip(
			false,
			{ onFilter(this@Row[index].tagName) },
			{ Text(this@Row[index].tagName) },
		)
	}
}

@Composable
fun BranchItem.WideCard(modifier: Modifier = Modifier, onAddToCart: () -> Unit = {}) {
	val cardHeight = 128.dp

	OutlinedCard(modifier.width(cardHeight * 3).height(cardHeight)) {
		Box {
			Row(Modifier.fillMaxWidth()) {
				KamelImage(
					resource = { asyncPainterResource("https://placehold.co/256x256") },
					contentDescription = itemName,
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.size(cardHeight)
						.clip(RoundedCornerShape(8.dp)),
					onFailure = {
						Box(
							contentAlignment = Alignment.Center,
							modifier = Modifier
								.size(cardHeight)
								.clip(RoundedCornerShape(8.dp))
								.background(MaterialTheme.colorScheme.primary),
						) {}
					}
				)
				this@WideCard.Metadata()
			}

			// Add to cart button positioned at top-right
			AddToCartButton(Modifier.align(Alignment.TopEnd), onAddToCart)

			if (isOutOfStock()) OutOfStockOverlay()
		}
	}
}

@Composable
fun BranchItem.TallCard(onAddToCart: () -> Unit = {}) {
	val cardWidth = 128.dp

	OutlinedCard(Modifier.size(cardWidth, cardWidth * 2)) {
		Box {
			Column {
				KamelImage(
					resource = { asyncPainterResource("https://placehold.co/256x256") },
					contentDescription = itemName,
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.size(cardWidth)
						.aspectRatio(1f)
						.clip(RoundedCornerShape(8.dp)),
					onFailure = {
						Box(
							contentAlignment = Alignment.Center,
							modifier = Modifier
								.size(cardWidth)
								.clip(RoundedCornerShape(8.dp))
								.background(MaterialTheme.colorScheme.primary),
						) {}
					}
				)

				this@TallCard.Metadata()
			}

			// Add to cart button positioned at top-right
			AddToCartButton(Modifier.align(Alignment.TopEnd), onAddToCart)

			if (isOutOfStock()) OutOfStockOverlay()
		}
	}
}

@Composable
fun PayAtCounterView(order: Order?) = Card(
	Modifier.fillMaxWidth(),
	elevation = CardDefaults.cardElevation(4.dp)
) {
	Column(
		Modifier.padding(24.dp),
		Arrangement.spacedBy(16.dp),
		Alignment.CenterHorizontally,
	) {
		Text(
			"Order Received",
			color = MaterialTheme.colorScheme.primary,
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.Bold,
		)
		Text(
			"Your order ${order?.orderId ?: "..."} has been received. Please proceed to the counter to complete your payment.",
			Modifier.padding(horizontal = 16.dp),
			style = MaterialTheme.typography.bodyLarge,
		)
	}
}

@Composable
fun SelfCheckoutView(order: Order?) {
	val amount = order?.finalAmount ?: 0
	val currency = Currency.VND

	val paymentCode = if (order == null || amount <= 0) "" else PaymentCode.Builder()
		.set(PaymentCode.PIMethod.DYNAMIC)
		.set(PaymentCode.ServiceCode.TRANSFER_TO_ACCOUNT)
		.setAccount(Bank.HDBank, "002704070021976")
		.setCountryCode()
		.setTransaction(amount, currency)
		.setPurpose("Payment for order ${order.orderId}")
		.build()

	val paymentQRCode = rememberQrCodePainter(paymentCode)

	Card(
		Modifier.fillMaxWidth().testTag("selfCheckoutView"),
		elevation = CardDefaults.cardElevation(4.dp)
	) {
		Column(
			Modifier.fillMaxWidth().padding(16.dp),
			Arrangement.spacedBy(8.dp),
			Alignment.CenterHorizontally,
		) {
			Text(
				"Self-checkout",
				Modifier.testTag("checkoutTitle"),
				style = MaterialTheme.typography.titleMedium
			)
			Text(
				"$amount $currency",
				Modifier.testTag("orderAmount"),
				color = MaterialTheme.colorScheme.primary,
				style = MaterialTheme.typography.displaySmall,
				fontWeight = FontWeight.Bold,
			)
			Text(
				"We accept VietQR bank transfer",
				Modifier.testTag("paymentMethodInfo"),
				style = MaterialTheme.typography.titleMedium
			)
			Image(
				paymentQRCode, "Payment QR Code",
				Modifier.testTag("paymentQRCode")
			)
		}
	}
}

@Composable
private fun OutOfStockOverlay() = Box(
	Modifier.fillMaxSize()
		.background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)),
	Alignment.Center,
) {
	Text(
		"Out of Stock",
		color = MaterialTheme.colorScheme.surface,
		style = MaterialTheme.typography.titleMedium,
		fontWeight = FontWeight.Bold,
	)
}

@Composable
private fun BranchItem.Metadata() = Column(Modifier.padding(8.dp)) {
	Text(
		itemName,
		maxLines = 2,
		overflow = TextOverflow.Ellipsis,
		style = MaterialTheme.typography.titleMedium
	)
	itemDes?.let {
		Text(
			it,
			maxLines = 2,
			overflow = TextOverflow.Ellipsis,
			style = MaterialTheme.typography.bodyMedium
		)
	}
	Text("$price VND", style = MaterialTheme.typography.bodyLarge)
}

@Preview(apiLevel = 35, showBackground = true)
@Composable
private fun OrderPagePreview() = Column(
	Modifier.padding(16.dp),
	Arrangement.spacedBy(8.dp)
) {
	listOf(BranchItem.MOCK, BranchItem.MOCK, BranchItem.MOCK).ImageSlider()

	Row(Modifier, Arrangement.spacedBy(8.dp)) {
		BranchItem.MOCK.TallCard()
		BranchItem.MOCK.TallCard()
	}

	Tag.MOCKS.Row()

	BranchItem.MOCK.WideCard()
	BranchItem.MOCK.WideCard()
}

@Preview(showBackground = true)
@Composable
private fun PayAtCounterPreview() = Column(
	Modifier.padding(16.dp).fillMaxWidth(),
	Arrangement.spacedBy(24.dp),
	Alignment.CenterHorizontally,
) {
	PayAtCounterView(Order.MOCK)
}

@Preview(showBackground = true)
@Composable
private fun SelfCheckoutPreview() = Column(
	Modifier.padding(16.dp).fillMaxWidth(),
	Arrangement.spacedBy(24.dp),
	Alignment.CenterHorizontally,
) {
	SelfCheckoutView(Order.MOCK)
}
