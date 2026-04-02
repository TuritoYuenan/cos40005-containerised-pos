package containerised.pos

object WebNotificationService : NotificationService {
	override fun showNotification(title: String, message: String) {
		// Web-specific implementation to show a notification
		println("Web Notification - Title: $title, Message: $message")
	}
}

actual val notificationService: NotificationService
	get() = WebNotificationService
