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

	private val paymentCode = PaymentCode()

	fun set(pim: PIMethod): PaymentCodeBuilder {
		paymentCode.poIM = pim.code
		return this
	}

	fun set(code: ServiceCode): PaymentCodeBuilder {
		paymentCode.serviceCode = code.code
		return this
	}

	fun setAccount(bank: Bank, account: String): PaymentCodeBuilder {
		paymentCode.acquirerID = bank.bin.toString()
		paymentCode.merchantID = account
		return this
	}

	/**
	 * Sets the transaction amount and currency code.
	 */
	fun setTransaction(amount: Double, currency: Currency): PaymentCodeBuilder {
		paymentCode.transactionAmount = amount.toString()
		paymentCode.transactionCurrency = currency.numericCode
		return this
	}

	fun setTransaction(amount: Int, currency: Currency): PaymentCodeBuilder {
		paymentCode.transactionAmount = amount.toString()
		paymentCode.transactionCurrency = currency.numericCode
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
