package containerised.pos.models

enum class Bank(val bin: Int) {
	AgriBank(970499),
	HDBank(970437),
	VietInBank(970415);

	companion object {
		fun fromBIN(bin: Int): Bank? = entries.find { it.bin == bin }
	}
}
