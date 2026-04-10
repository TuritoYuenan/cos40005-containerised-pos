package containerised.pos.services

/**
 * Provides a platform-specific implementation for showing notifications.
 */
fun interface NotificationService {
	/**
	 * Shows a notification with the given title and message.
	 *
	 * @param title The title of the notification.
	 * @param message The message content of the notification.
	 */
	fun showNotification(title: String, message: String)
}

expect val notificationService: NotificationService
