package containerised.pos

import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

object JvmNotificationService : NotificationService {
	private val trayIcon: TrayIcon? by lazy {
		if (!SystemTray.isSupported()) {
			println("System tray not supported. Notifications will be disabled.")
			return@lazy null
		}

		println("Creating system tray icon for notifications.")
		val tray = SystemTray.getSystemTray()
		val image = Toolkit.getDefaultToolkit().createImage("icon.png")

		val trayIcon = TrayIcon(image, "Order App")
		trayIcon.isImageAutoSize = true

		tray.add(trayIcon)
		trayIcon
	}

	override fun showNotification(title: String, message: String) {
		trayIcon?.displayMessage(title, message, TrayIcon.MessageType.INFO)
	}
}

actual val notificationService: NotificationService
	get() = JvmNotificationService
