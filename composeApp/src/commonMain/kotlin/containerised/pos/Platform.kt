package containerised.pos

interface Platform {
    val name: String
}

expect val isWeb: Boolean
expect fun getPlatform(): Platform
