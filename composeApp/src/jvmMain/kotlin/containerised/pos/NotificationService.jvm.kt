package containerised.pos

import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

actual object NotificationService {

	private val trayIcon: TrayIcon? by lazy {
		if (!SystemTray.isSupported()) return@lazy null

		val tray = SystemTray.getSystemTray()
		val image = Toolkit.getDefaultToolkit()
			.createImage("icon.png")

		val trayIcon = TrayIcon(image, "Order App")
		trayIcon.isImageAutoSize = true

		tray.add(trayIcon)
		trayIcon
	}

	actual fun showNotification(title: String, message: String) {
		trayIcon?.displayMessage(title, message, TrayIcon.MessageType.INFO)
	}
}
