package containerised.pos.models

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmployeeShiftTest {
	private val shift = EmployeeShift(
		userId = "user-1",
		startTime = "09:00",
		endTime = "17:00",
		date = DayOfWeek.MON
	)

	@Test
	fun `isInShift includes start hour`() {
		assertTrue(EmployeeShift.isInShift(9, shift))
	}

	@Test
	fun `isInShift excludes end hour`() {
		assertFalse(EmployeeShift.isInShift(17, shift))
	}

	@Test
	fun `isInShift returns false before shift starts`() {
		assertFalse(EmployeeShift.isInShift(8, shift))
	}

	@Test
	fun `isInShift returns true during shift`() {
		assertTrue(EmployeeShift.isInShift(16, shift))
	}
}
