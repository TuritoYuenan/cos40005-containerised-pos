package containerised.pos

import containerised.pos.models.Branch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class SupabaseTest {
	@Test
	fun `fetch branches`() = runBlocking {
		val branches = Branch.fetchAll()
		assert(branches.isNotEmpty()) { "Expected to fetch at least one branch, but got none." }
	}
}
