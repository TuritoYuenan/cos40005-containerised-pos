package containerised.pos.services

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

object IOSDownloadService : DownloadService {
	override fun download(url: String) {
		if (url.isBlank()) return

		val nsUrl = NSURL.URLWithString(url) ?: return
		UIApplication.sharedApplication.openURL(nsUrl)
	}
}

actual val downloadService: DownloadService
	get() = IOSDownloadService
