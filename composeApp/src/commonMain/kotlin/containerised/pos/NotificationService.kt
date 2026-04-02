package containerised.pos

interface NotificationService {
	fun showNotification(title: String, message: String)
}

expect val notificationService: NotificationService
