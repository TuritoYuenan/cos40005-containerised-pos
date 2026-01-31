package containerised.pos.models

enum class Currency(val code: String, val numericCode: String) {
	VND("VND", "704"),
	USD("USD", "840"),
	EUR("EUR", "978");

	companion object {
		/**
		 * Get currency by its alphabetic code (e.g., "VND")
		 * @return Currency instance or null if not found
		 */
		fun fromCode(code: String): Currency? = entries.find { it.code == code }
	}
}
