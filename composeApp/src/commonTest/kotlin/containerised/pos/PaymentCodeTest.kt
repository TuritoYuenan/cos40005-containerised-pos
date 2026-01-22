package containerised.pos

import containerised.pos.models.PaymentCodeBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class PaymentCodeTest {

	/**
	 * This tests how it can replicate a specific code I got from hanging out the other day.
	 */
	@Test
	fun testReplicatingRealPaymentCode() {
		val builder = PaymentCodeBuilder()
			.set(PaymentCodeBuilder.PIMethod.DYNAMIC)
			.set(PaymentCodeBuilder.ServiceCode.TRANSFER_TO_ACCOUNT)
			.setAccount(acquirerID = "970415", merchantID = "106877386224")
			.setTransaction("145000", PaymentCodeBuilder.Currency.VND)
			.setCountryCode("VN")
			.setPurpose("Dokki Vincom Dong Khoi")

		val paymentCode = builder.build()

		// Print or assert the generated payment code
		println("Generated Payment Code: $paymentCode")

		assertEquals("00020101021238560010A0000007270126000697041501121068773862240208QRIBFTTA530370454061450005802VN62260822Dokki Vincom Dong Khoi63041691", paymentCode)
	}
}
