package containerised.pos.models

import kotlin.test.Test
import kotlin.test.assertEquals

class BranchTest {
	@Test
	fun `test branch properties`() {
		val branch = Branch.MOCK
		assertEquals("branch123", branch.branchId)
		assertEquals("Main Branch", branch.branchName)
		assertEquals("123 Main St", branch.address)
		assertEquals("555-1234", branch.phoneNumber)
		assertEquals("branch@business.com", branch.email)
		assertEquals(true, branch.isActive)
		assertEquals("2024-01-01T00:00:00Z", branch.createdAt)
	}
}
