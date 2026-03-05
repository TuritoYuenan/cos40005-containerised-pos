package containerised.pos

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
