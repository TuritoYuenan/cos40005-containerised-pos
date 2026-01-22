package containerised.pos.models

/**
 * Creates a payment code for processing payments.
 */
class PaymentCodeBuilder {
	enum class PIMethod(val code: String) {
		STATIC("11"),
		DYNAMIC("12")
	}

	enum class ServiceCode(val code: String) {
		TRANSFER_TO_ACCOUNT("QRIBFTTA"),
		TRANSFER_TO_CARD("QRIBFTTC")
	}

	enum class Currency(val code: String) {
		VND("704"),
		USD("840"),
		EUR("978")
	}

	val paymentCode = PaymentCode()

	fun set(pim: PIMethod): PaymentCodeBuilder {
		paymentCode.poIM = pim.code
		return this
	}

	fun set(code: ServiceCode): PaymentCodeBuilder {
		paymentCode.serviceCode = code.code
		return this
	}

	fun setAccount(acquirerID: String, merchantID: String): PaymentCodeBuilder {
		paymentCode.acquirerID = acquirerID
		paymentCode.merchantID = merchantID
		return this
	}

	fun setTransaction(amount: String, currency: Currency = Currency.VND): PaymentCodeBuilder {
		paymentCode.transactionAmount = amount
		paymentCode.transactionCurrency = currency.code
		return this
	}

	fun setCountryCode(countryCode: String = "VN"): PaymentCodeBuilder {
		paymentCode.countryCode = countryCode
		return this
	}

	fun setPurpose(purpose: String): PaymentCodeBuilder {
		paymentCode.purpose = purpose
		return this
	}

	fun build(): String {
		return paymentCode.toPayload()
	}
}
