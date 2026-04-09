package containerised.pos.services

import kotlin.test.Test
import kotlin.test.assertEquals

class CRCServiceTest {
	@Test
	fun `crc16 byte array matches known CCITT-FALSE test vector`() {
		val payload = "123456789".encodeToByteArray()

		val crc = CRCService.crc16(payload)

		assertEquals(0x29B1.toUShort(), crc)
	}

	@OptIn(ExperimentalUnsignedTypes::class)
	@Test
	fun `crc16 byte array and ubyte array overloads return same value`() {
		val payload = "Containerised POS".encodeToByteArray()

		val fromBytes = CRCService.crc16(payload)
		val fromUBytes = CRCService.crc16(payload.map { it.toUByte() }.toUByteArray())

		assertEquals(fromBytes, fromUBytes)
	}

	@Test
	fun `crc16 of empty input equals initial value`() {
		val crc = CRCService.crc16(byteArrayOf())

		assertEquals(0xFFFF.toUShort(), crc)
	}
}
