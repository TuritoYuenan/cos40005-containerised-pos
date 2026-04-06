package containerised.pos.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri

object AndroidDownloadService : DownloadService {
	private var appContext: Context? = null

	fun initialize(context: Context) {
		appContext = context.applicationContext
	}

	override fun download(url: String) {
		if (url.isBlank()) return

		val context = appContext ?: run {
			Log.w("DOWNLOAD", "Download service is not initialized")
			return
		}

		val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
			addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		}

		runCatching {
			context.startActivity(intent)
		}.onFailure {
			Log.e("DOWNLOAD", "Failed to open download URL", it)
		}
	}
}

actual val downloadService: DownloadService = AndroidDownloadService
