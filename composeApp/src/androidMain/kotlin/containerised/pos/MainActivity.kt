package containerised.pos

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import containerised.pos.services.AndroidNotificationService

class MainActivity : ComponentActivity() {
	private val requestPermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted: Boolean ->
		if (!isGranted) Log.i("NOTIF", "Notification permission denied")
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)

		AppGraph.init(applicationContext) // DI init

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			requestPermissionLauncher.launch(POST_NOTIFICATIONS)
		}

		AndroidNotificationService.initialize(this)

		setContent { AppNavHost() }
	}
}
