package containerised.pos.services

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

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

	override fun download(bytes: ByteArray, fileName: String, mimeType: String) {
		if (bytes.isEmpty()) return

		val context = appContext ?: run {
			Log.w("DOWNLOAD", "Download service is not initialized")
			return
		}

		val outputFile = File(
			context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
				?: context.filesDir,
			fileName
		)

		runCatching {
			FileOutputStream(outputFile).use { outputStream ->
				outputStream.write(bytes)
				outputStream.flush()
			}
			Log.i("DOWNLOAD", "Saved $mimeType file to ${outputFile.absolutePath}")
		}.onFailure {
			Log.e("DOWNLOAD", "Failed to save download", it)
		}
	}
}

actual val downloadService: DownloadService = AndroidDownloadService
