package containerised.pos.services

import kotlinx.browser.window

object WebDownloadService: DownloadService {
	override fun download(url: String) {
		if (url.isBlank()) return

		val openedWindow = window.open(url, "_blank")
		if (openedWindow == null) window.location.href = url
	}
}

actual val downloadService: DownloadService
    get() = WebDownloadService
