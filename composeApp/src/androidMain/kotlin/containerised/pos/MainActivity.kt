package containerised.pos

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
//            App()
			SupabaseBranchTest()
        }
    }
}

//@Preview
//@Composable
//fun AppAndroidPreview() {
//    App()
//}

@Composable
fun SupabaseBranchTest() {
	LaunchedEffect(Unit) {
		try {
			Log.d("SupabaseBranch", "=== Testing Supabase branches table ===")

			// Add a new branch
			val newBranch = Branch(
				branch_name = "Hanoi Central",
				address = "123 Main St, Hanoi",
				phone_number = "+84 912 345 678",
				email = "central@shop.com",
				is_active = true
			)

			addBranch(newBranch)
			Log.d("SupabaseBranch", "Added branch: ${newBranch.branch_name}")

			// Fetch all branches
			val branches = fetchBranches()
			Log.d("SupabaseBranch", "Fetched ${branches.size} branches:")
			branches.forEach { branch ->
				Log.d(
					"SupabaseBranch",
					"• ${branch.branch_id}: ${branch.branch_name} (${branch.email}) - Active: ${branch.is_active}"
				)
			}

		} catch (e: Exception) {
			Log.e("SupabaseBranch", "Error: ${e.message}", e)
		}
	}
}
