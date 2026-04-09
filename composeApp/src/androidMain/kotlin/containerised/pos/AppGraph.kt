package containerised.pos

import android.content.Context
import containerised.pos.services.AndroidRealtimeServiceController
import containerised.pos.services.RealtimeServiceController

object AppGraph {
    lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val realtimeServiceController: RealtimeServiceController by lazy {
        AndroidRealtimeServiceController(appContext)
    }
}
