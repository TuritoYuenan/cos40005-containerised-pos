package containerised.pos.services

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

object IOSDownloadService : DownloadService {
	override fun download(url: String) {
		if (url.isBlank()) return

		val nsUrl = NSURL.URLWithString(url) ?: return
		UIApplication.sharedApplication.openURL(nsUrl)
	}

	override fun download(bytes: ByteArray, fileName: String, mimeType: String) {
		if (bytes.isEmpty()) return

		val documentsDirectory = NSHomeDirectory() + "/Documents"
		val outputPath = "$documentsDirectory/$fileName"
		val data = bytes.usePinned {
			NSData.dataWithBytes(it.addressOf(0), bytes.size.toULong())
		}

		data.writeToFile(outputPath, atomically = true)
	}
}

actual val downloadService: DownloadService
	get() = IOSDownloadService
