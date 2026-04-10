package containerised.pos.services

actual val notificationService = NotificationService {
	title, message -> println("iOS Notification - Title: $title, Message: $message")
}
