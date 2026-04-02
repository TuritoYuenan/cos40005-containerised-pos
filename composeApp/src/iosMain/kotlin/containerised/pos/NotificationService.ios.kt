package containerised.pos

object IOSNotificationService : NotificationService {
	override fun showNotification(title: String, message: String) {
		// iOS-specific implementation to show a notification
		println("iOS Notification - Title: $title, Message: $message")
	}
}

actual val notificationService: NotificationService
	get() = IOSNotificationService
