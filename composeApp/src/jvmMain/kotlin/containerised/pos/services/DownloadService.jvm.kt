package containerised.pos.services

import java.awt.Desktop
import java.nio.file.Files
import java.net.URI
import java.nio.file.Paths

object JvmDownloadService : DownloadService {
	override fun download(url: String) {
		if (url.isBlank()) return

		runCatching {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(URI(url))
			} else {
				println("Desktop browsing is not supported on this system.")
			}
		}.onFailure {
			println("Failed to open download URL: $it")
		}
	}

	override fun download(bytes: ByteArray, fileName: String, mimeType: String) {
		if (bytes.isEmpty()) return

		val downloadsDirectory = Paths.get(System.getProperty("user.home"), "Downloads")
		Files.createDirectories(downloadsDirectory)

		val outputFile = downloadsDirectory.resolve(fileName)
		Files.write(outputFile, bytes)

		runCatching {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(outputFile.toFile())
			}
		}.onFailure {
			println("Saved file to $outputFile but could not open it: $it")
		}
	}
}

actual val downloadService: DownloadService
	get() = JvmDownloadService
