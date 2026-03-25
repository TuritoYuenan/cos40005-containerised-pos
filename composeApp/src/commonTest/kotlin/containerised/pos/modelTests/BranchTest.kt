package containerised.pos.modelTests

import containerised.pos.models.Branch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

	@Test
	fun `test fetch branches`() {
		runBlocking {
			val branches = Branch.fetchAll()
			assertTrue(branches.isNotEmpty(), "Expected Supabase to return at least one branch")
			assertTrue(branches.any { it.branchId.isNotBlank() }, "Expected fetched branches to have valid IDs")
		}
	}
}
