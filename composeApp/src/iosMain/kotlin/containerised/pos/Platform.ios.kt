package containerised.pos

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual val isWeb: Boolean = false
actual fun getPlatform(): Platform = IOSPlatform()
