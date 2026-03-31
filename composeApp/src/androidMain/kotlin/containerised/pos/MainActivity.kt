package containerised.pos

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
	private val requestPermissionLauncher =
		registerForActivityResult(
			ActivityResultContracts.RequestPermission()
		) { isGranted: Boolean ->
			if (!isGranted) {
				println("Notification permission denied")
			}
		}

	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		AppContextHolder.context = applicationContext
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			requestPermissionLauncher.launch(
				android.Manifest.permission.POST_NOTIFICATIONS
			)
		}
		NotificationService.initialize(this)

		setContent {
			AppNavHost()
		}
	}
}

object AppContextHolder {
	lateinit var context: Context
}
