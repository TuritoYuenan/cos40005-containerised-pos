package containerised.pos

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual val isWeb: Boolean = false
actual fun getPlatform(): Platform = AndroidPlatform()
