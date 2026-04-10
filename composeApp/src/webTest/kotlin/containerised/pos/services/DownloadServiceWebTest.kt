package containerised.pos.services

import kotlin.test.Test
import kotlin.test.assertTrue

class DownloadServiceWebTest {
	@Test
	fun `downloadService resolves to web implementation`() {
		assertTrue(downloadService === WebDownloadService)
	}

	@Test
	fun `download with blank url does not throw`() {
		WebDownloadService.download("   ")
	}

	@Test
	fun `download with empty bytes does nothing`() {
		WebDownloadService.download(byteArrayOf(), "ignored.bin", "application/octet-stream")
	}
}
