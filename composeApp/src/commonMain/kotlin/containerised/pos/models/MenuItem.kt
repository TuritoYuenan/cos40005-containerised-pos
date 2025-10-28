package containerised.pos.models

import kotlinx.serialization.Serializable

@Serializable
data class MenuItem(
	val item_id: String,
	val item_name: String,
	val item_des: String,
	val item_price: Double,
	val category_id: String,
	val is_available: Boolean,
	val est_prep_time: Int,
	val img_url: String,
	val branch_id: String,
	val special_notes: String?
)
