package containerised.pos

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual val isWeb: Boolean = true
actual fun getPlatform(): Platform = WasmPlatform()
