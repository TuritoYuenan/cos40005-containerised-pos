package containerised.pos

expect object NotificationService {
	fun showNotification(
		title: String,
		message: String
	)
}
