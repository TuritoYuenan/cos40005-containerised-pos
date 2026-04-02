package containerised.pos.services

interface NotificationService {
	fun showNotification(title: String, message: String)
}

expect val notificationService: NotificationService
