package containerised.pos.services

import android.os.Environment
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AndroidDownloadServiceTest {
	private val context = InstrumentationRegistry.getInstrumentation().targetContext

	@Test
	fun downloadWithBlankUrlDoesNotThrow() {
		AndroidDownloadService.download("   ")
	}

	@Test
	fun downloadWithEmptyBytesDoesNotCreateFile() {
		val file = expectedOutputFile("empty-bytes-no-op.bin")
		if (file.exists()) file.delete()

		AndroidDownloadService.download(byteArrayOf(), file.name, "application/octet-stream")

		assertFalse("Expected no file to be created for empty content", file.exists())
	}

	@Test
	fun downloadWritesBytesAfterInitialization() {
		AndroidDownloadService.initialize(context)

		val expectedBytes = "android-download-test".encodeToByteArray()
		val file = expectedOutputFile("download-service-android-test.txt")
		if (file.exists()) file.delete()

		try {
			AndroidDownloadService.download(expectedBytes, file.name, "text/plain")

			assertTrue("Expected file to be written", file.exists())
			assertArrayEquals(expectedBytes, file.readBytes())
		} finally {
			file.delete()
		}
	}

	private fun expectedOutputFile(fileName: String): File {
		val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
		return File(directory, fileName)
	}
}
