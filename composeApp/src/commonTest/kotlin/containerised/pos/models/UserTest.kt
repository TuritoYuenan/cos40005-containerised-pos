package containerised.pos.models

import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class UserTest {
	@Test
	fun `days constant exposes all 7 domain days in enum order`() {
		assertEquals(DayOfWeek.entries.toList(), days)
	}

	@Test
	fun `hours constant spans 06 to 22 inclusive`() {
		assertEquals(17, hours.size)
		assertEquals("06:00", hours.first())
		assertEquals("22:00", hours.last())
	}

	@Test
	fun `mapDay maps all kotlinx days to domain days`() {
		assertEquals(DayOfWeek.MON, User.mapDay(kotlinx.datetime.DayOfWeek.MONDAY))
		assertEquals(DayOfWeek.TUE, User.mapDay(kotlinx.datetime.DayOfWeek.TUESDAY))
		assertEquals(DayOfWeek.WED, User.mapDay(kotlinx.datetime.DayOfWeek.WEDNESDAY))
		assertEquals(DayOfWeek.THU, User.mapDay(kotlinx.datetime.DayOfWeek.THURSDAY))
		assertEquals(DayOfWeek.FRI, User.mapDay(kotlinx.datetime.DayOfWeek.FRIDAY))
		assertEquals(DayOfWeek.SAT, User.mapDay(kotlinx.datetime.DayOfWeek.SATURDAY))
		assertEquals(DayOfWeek.SUN, User.mapDay(kotlinx.datetime.DayOfWeek.SUNDAY))
	}

	@Test
	fun `getStatusFromShift returns OFF_DUTY when no shift for current day`() {
		val shift = mapOf(
			DayOfWeek.TUE to listOf("10:00")
		)

		val status = User.getStatusFromShift(
			shift,
			now = LocalDateTime.parse("2026-04-13T10:15:00")
		)

		assertEquals(User.EmployeeStatus.OFF_DUTY, status)
	}

	@Test
	fun `getStatusFromShift returns OFF_DUTY when today's shift list is empty`() {
		val shift = mapOf(
			DayOfWeek.MON to emptyList<String>()
		)

		val status = User.getStatusFromShift(
			shift,
			now = LocalDateTime.parse("2026-04-13T10:15:00")
		)

		assertEquals(User.EmployeeStatus.OFF_DUTY, status)
	}

	@Test
	fun `getStatusFromShift returns INACTIVE when current hour matches slot`() {
		val shift = mapOf(
			DayOfWeek.MON to listOf("10:00", "11:00")
		)

		val status = User.getStatusFromShift(
			shift,
			now = LocalDateTime.parse("2026-04-13T10:59:00")
		)

		assertEquals(User.EmployeeStatus.INACTIVE, status)
	}

	@Test
	fun `getStatusFromShift returns OFF_DUTY when current hour does not match any slot`() {
		val shift = mapOf(
			DayOfWeek.MON to listOf("10:00", "11:00")
		)

		val status = User.getStatusFromShift(
			shift,
			now = LocalDateTime.parse("2026-04-13T12:00:00")
		)

		assertEquals(User.EmployeeStatus.OFF_DUTY, status)
	}
}
