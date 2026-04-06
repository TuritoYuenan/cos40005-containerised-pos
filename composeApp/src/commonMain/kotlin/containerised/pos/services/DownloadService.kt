package containerised.pos.services

/** Provides methods to download a local or internet file */
interface DownloadService {
	/** Download an online file via its public URL */
	fun download(url: String)
}

expect val downloadService: DownloadService
