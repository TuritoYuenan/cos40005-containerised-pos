package containerised.pos.services

actual val notificationService = NotificationService {
	title, message -> println("Web Notification - Title: $title, Message: $message")
}
