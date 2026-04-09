package containerised.pos.models

import containerised.pos.services.CRCService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaymentCodeTest {
	/**
	 * This tests how it can replicate a specific code I got from hanging out the other day.
	 */
	@Test
	fun testReplicatingRealPaymentCode() {
		val builder = PaymentCode.Builder()
			.set(PaymentCode.PIMethod.DYNAMIC)
			.set(PaymentCode.ServiceCode.TRANSFER_TO_ACCOUNT)
			.setAccount(Bank.VietInBank, "106877386224")
			.setTransaction(145000, Currency.VND)
			.setCountryCode("VN")
			.setPurpose("Dokki Vincom Dong Khoi")

		val paymentCode = builder.build()

		// Print or assert the generated payment code
		println("Generated Payment Code: $paymentCode")

		assertEquals(
			"00020101021238560010A0000007270126000697041501121068773862240208QRIBFTTA530370454061450005802VN62260822Dokki Vincom Dong Khoi63041691",
			paymentCode
		)
	}

	@Test
	fun `test payment code includes selected static card transfer settings`() {
		val paymentCode = PaymentCode.Builder()
			.set(PaymentCode.PIMethod.STATIC)
			.set(PaymentCode.ServiceCode.TRANSFER_TO_CARD)
			.setAccount(Bank.HDBank, "1234567890")
			.setTransaction(999, Currency.USD)
			.setCountryCode()
			.setPurpose("Test")
			.build()

		assertTrue(paymentCode.contains("010211"), "Expected STATIC point-of-initiation method")
		assertTrue(paymentCode.contains("0208QRIBFTTC"), "Expected transfer-to-card service code")
		assertTrue(paymentCode.contains(Bank.HDBank.bin.toString()), "Expected selected bank BIN")
		assertTrue(paymentCode.contains("5303840"), "Expected USD numeric currency code")
		assertTrue(paymentCode.contains("5403999"), "Expected amount with 3-digit length")
		assertTrue(paymentCode.contains("5802VN"), "Expected default VN country code")
	}

	@Test
	fun `test payment code has valid crc suffix`() {
		val paymentCode = PaymentCode.Builder()
			.set(PaymentCode.PIMethod.DYNAMIC)
			.set(PaymentCode.ServiceCode.TRANSFER_TO_ACCOUNT)
			.setAccount(Bank.AgriBank, "123456")
			.setTransaction(1000, Currency.EUR)
			.setCountryCode("VN")
			.setPurpose("CRC test")
			.build()

		val bodyWithCrcHeader = paymentCode.dropLast(4)
		val actualCrc = paymentCode.takeLast(4)
		val expectedCrc = CRCService.crc16(bodyWithCrcHeader.encodeToByteArray())
			.toString(16)
			.uppercase()
			.padStart(4, '0')

		assertEquals(expectedCrc, actualCrc)
	}
}
