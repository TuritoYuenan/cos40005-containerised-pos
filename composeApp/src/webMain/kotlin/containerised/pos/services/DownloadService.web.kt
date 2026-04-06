package containerised.pos.services

import kotlinx.browser.document
import kotlinx.browser.window
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.w3c.dom.HTMLAnchorElement

object WebDownloadService: DownloadService {
	override fun download(url: String) {
		if (url.isBlank()) return

		val openedWindow = window.open(url, "_blank")
		if (openedWindow == null) window.location.href = url
	}

	@OptIn(ExperimentalEncodingApi::class)
	override fun download(bytes: ByteArray, fileName: String, mimeType: String) {
		if (bytes.isEmpty()) return

		val downloadUrl = "data:$mimeType;base64,${Base64.Default.encode(bytes)}"
		val anchor = document.createElement("a") as HTMLAnchorElement
		anchor.href = downloadUrl
		anchor.download = fileName
		anchor.style.display = "none"

		document.body?.appendChild(anchor)
		anchor.click()
		anchor.remove()
	}
}

actual val downloadService: DownloadService
    get() = WebDownloadService
