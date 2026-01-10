package containerised.pos

class JVMPlatform : Platform {
	override val name: String = "Java ${System.getProperty("java.version")}"
}

actual val isWeb: Boolean = false
actual fun getPlatform(): Platform = JVMPlatform()
