package containerised.pos.services

/** Provides methods to download a local or internet file */
interface DownloadService {
	/** Download an online file via its public URL */
	fun download(url: String)

	/** Download a local file from raw bytes */
	fun download(bytes: ByteArray, fileName: String, mimeType: String = "application/octet-stream")
}

expect val downloadService: DownloadService
