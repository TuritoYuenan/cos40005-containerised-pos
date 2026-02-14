package containerised.pos.services

/**
 * Service for calculating CRC16 checksums, mostly used in generating VietQR payment codes.
 * This implementation uses the CRC-16-CCITT polynomial (0x1021) and an initial value of 0xFFFF.
 */
object CRCService {
	private infix fun UShort.shl(bitCount: Int): UShort = (this.toUInt() shl bitCount).toUShort()
	private infix fun UShort.shr(bitCount: Int): UShort = (this.toUInt() shr bitCount).toUShort()

	fun crc16(input: UByte, polynomial: UShort): UShort {
		val bigEndianInput = input.toUShort() shl 8

		return (0 until 8).fold(bigEndianInput) { result, _ ->
			val isMostSignificantBitOne = result and 0x8000.toUShort() != 0.toUShort()
			val shiftedResult = result shl 1

			when (isMostSignificantBitOne) {
				true -> shiftedResult xor polynomial
				false -> shiftedResult
			}
		}
	}

	@OptIn(ExperimentalUnsignedTypes::class)
	fun crc16(inputs: UByteArray, initialValue: UShort = 0xFFFF.toUShort()): UShort {
		return inputs.fold(initialValue) { remainder, byte ->
			val bigEndianInput = byte.toUShort() shl 8
			val index = (bigEndianInput xor remainder) shr 8
			crc16Table[index.toInt()] xor (remainder shl 8)
		}
	}

	@OptIn(ExperimentalUnsignedTypes::class)
	fun crc16(inputs: ByteArray, initialValue: Short = -1): UShort =
		crc16(inputs.map(Byte::toUByte).toUByteArray(), initialValue.toUShort())

	private val crc16Table: List<UShort> = (0 until 256).map {
		crc16(it.toUByte(), 0x1021.toUShort())
	}
}
