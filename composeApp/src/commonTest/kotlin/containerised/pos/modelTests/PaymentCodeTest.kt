package containerised.pos.modelTests

import containerised.pos.models.Bank
import containerised.pos.models.Currency
import containerised.pos.models.PaymentCode
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
