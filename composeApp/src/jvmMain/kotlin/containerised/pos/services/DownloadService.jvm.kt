package containerised.pos.services

import java.awt.Desktop
import java.net.URI

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
}

actual val downloadService: DownloadService
	get() = JvmDownloadService
