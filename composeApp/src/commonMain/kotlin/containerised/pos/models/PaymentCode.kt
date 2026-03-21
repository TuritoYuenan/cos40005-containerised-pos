package containerised.pos.models

import containerised.pos.services.CRCService
import io.ktor.utils.io.core.*

/**
 * Represents a payment code for VietQR payment system
 */
class PaymentCode {
	//	1. Payload Format Indicator
	var pfi: String = "01"

	//	2. Point of Initiation Method
	var poIM: String = ""

	//	3. Consumer Account Information
	var guid: String = "A000000727"
	var acquirerID: String = ""
	var merchantID: String = ""
	var serviceCode: String = ""

	//	4. Transaction Currency
	var transactionCurrency: String = ""

	//	5. Transaction Amount
	var transactionAmount: String = ""

	//	6. Country Code
	var countryCode: String = ""

	//	7. Additional Data Field Template
	var purpose: String = ""

	/**
	 * Construct the payment code payload string based on the provided fields
	 * Each field has the format: ID (2 digits) + Length (2 digits) + Value
	 * Structure:
	 * - 00 Payload Format Indicator
	 * - 01 Point of Initiation Method
	 * - 38 Consumer Account Information
	 *    - 00 GUID
	 *    - 01 Acquirer and Merchant Information (00 Acquirer ID, 01 Merchant ID)
	 *    - 02 Service Code
	 * - 53 Transaction Currency
	 * - 54 Transaction Amount
	 * - 58 Country Code
	 * - 62 Additional Data Field Template (08 Purpose)
	 * - 63 CRC
	 */
	private fun toPayload(): String {
		val payload = StringBuilder()
		payload.append("0002").append(pfi)
		payload.append("0102").append(poIM)

		val field38 = StringBuilder()
		field38.append("00").append(guid.length.toString().padStart(2, '0')).append(guid)

		val field3801 = StringBuilder()
		field3801.append("00").append(acquirerID.length.toString().padStart(2, '0'))
			.append(acquirerID)
		field3801.append("01").append(merchantID.length.toString().padStart(2, '0'))
			.append(merchantID)
		field38.append("01").append(field3801.length.toString().padStart(2, '0'))
			.append(field3801.toString())

		field38.append("02").append(serviceCode.length.toString().padStart(2, '0'))
			.append(serviceCode)
		payload.append("38").append(field38.length.toString().padStart(2, '0'))
			.append(field38.toString())

		payload.append("53").append("03").append(transactionCurrency)
		payload.append("54").append(transactionAmount.length.toString().padStart(2, '0'))
			.append(transactionAmount)
		payload.append("58").append("02").append(countryCode)

		val additionalDataField = StringBuilder()
		additionalDataField.append("08")
			.append(purpose.length.toString().padStart(2, '0')).append(purpose)
		payload.append("62")
			.append(additionalDataField.length.toString().padStart(2, '0'))
			.append(additionalDataField.toString())

		// Calculate CRC
		payload.append("63").append("04")
		val crcInput = payload.toString().toByteArray()
		val crc = CRCService.crc16(crcInput)
		payload.append(crc.toString(16).uppercase().padStart(4, '0'))

		return payload.toString()
	}

	/**
	 * Point of Initiation Method
	 */
	enum class PIMethod(val code: String) {
		/**
		 * Static QR code: The QR code is generated once and can be reused for multiple transactions.
		 * The payer needs to enter the transaction amount manually.
		 * Suitable for fixed-price payments or when the merchant wants to display a single QR code at the point of sale.
		 */
		STATIC("11"),

		/**
		 * Dynamic QR code: The QR code is generated for each transaction and includes the transaction amount and other details.
		 * The payer can scan the QR code to pay the exact amount without manual input.
		 * Suitable for variable-price payments or when the merchant wants to display a unique QR code for each transaction, such as on a receipt or a digital display.
		 */
		DYNAMIC("12")
	}

	/**
	 * Service codes for different types of transactions
	 */
	enum class ServiceCode(val code: String) {
		/**
		 * The payer transfers money directly to the merchant's bank account using the provided acquirer and merchant information.
		 */
		TRANSFER_TO_ACCOUNT("QRIBFTTA"),

		/**
		 * The payer transfers money to the merchant's card number using the provided acquirer and merchant information.
		 */
		TRANSFER_TO_CARD("QRIBFTTC")
	}

	/**
	 * Builder class to construct a PaymentCode instance with a fluent API
	 */
	class Builder {
		private val paymentCode = PaymentCode()

		fun set(pim: PIMethod): Builder {
			paymentCode.poIM = pim.code
			return this
		}

		fun set(code: ServiceCode): Builder {
			paymentCode.serviceCode = code.code
			return this
		}

		fun setAccount(bank: Bank, account: String): Builder {
			paymentCode.acquirerID = bank.bin.toString()
			paymentCode.merchantID = account
			return this
		}

		fun setTransaction(amount: Int, currency: Currency): Builder {
			paymentCode.transactionAmount = amount.toString()
			paymentCode.transactionCurrency = currency.numericCode
			return this
		}

		fun setCountryCode(countryCode: String = "VN"): Builder {
			paymentCode.countryCode = countryCode
			return this
		}

		fun setPurpose(purpose: String): Builder {
			paymentCode.purpose = purpose
			return this
		}

		fun build(): String = paymentCode.toPayload()
	}
}
