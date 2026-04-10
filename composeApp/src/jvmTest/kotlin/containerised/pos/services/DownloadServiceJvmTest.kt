package containerised.pos.services

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DownloadServiceJvmTest {
	private var overriddenUserHome: String? = null

	@AfterTest
	fun tearDown() {
		overriddenUserHome?.let { original ->
			System.setProperty("user.home", original)
			overriddenUserHome = null
		}
	}

	@Test
	fun `downloadService resolves to jvm implementation`() {
		assertSame(downloadService, JvmDownloadService)
	}

	@Test
	fun `download with blank url does not throw`() {
		JvmDownloadService.download("   ")
	}

	@Test
	fun `download writes bytes to downloads directory`() {
		val originalHome = System.getProperty("user.home")
		overriddenUserHome = originalHome

		val tempHome = Files.createTempDirectory("download-service-jvm-home")
		System.setProperty("user.home", tempHome.toString())

		try {
			val bytes = "receipt-data".encodeToByteArray()
			val fileName = "download-test-${UUID.randomUUID()}.txt"

			JvmDownloadService.download(bytes, fileName, "text/plain")

			val expectedOutput = Paths.get(tempHome.toString(), "Downloads", fileName)
			assertTrue(Files.exists(expectedOutput), "Expected output file to be created")
			assertContentEquals(bytes, Files.readAllBytes(expectedOutput))
		} finally {
			deleteRecursively(tempHome)
		}
	}

	@Test
	fun `download with empty bytes does nothing`() {
		JvmDownloadService.download(byteArrayOf(), "ignored.bin", "application/octet-stream")
	}

	private fun deleteRecursively(root: Path) {
		if (!Files.exists(root)) return
		Files.walk(root)
			.sorted(Comparator.reverseOrder())
			.forEach(Files::deleteIfExists)
	}
}
