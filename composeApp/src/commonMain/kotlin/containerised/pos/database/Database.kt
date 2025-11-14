package containerised.pos.database

import containerised.pos.models.MenuItem

class Database(driverFactory: DatabaseDriverFactory) {
	private val database = AppDatabase(driverFactory.createDriver())
	private val dbQuery = database.appDatabaseQueries

	internal fun getAllMenuItems(): List<MenuItem> {
		return dbQuery.selectAllMenuItems(::mapMenuItemSelecting).executeAsList()
	}

	private fun mapMenuItemSelecting(
		item_id: String,
		item_name: String,
		item_des: String?,
		price: Double,
		category_id: String,
		is_available: Boolean?,
		est_prep_time: String?,
		img_url: String?,
		branch_id: String,
		special_notes: String?,
	): MenuItem {
		return MenuItem(
			id = item_id,
			name = item_name,
			description = item_des,
			price = price.toFloat(),
			categoryID = category_id,
			isAvailable = is_available,
			estimatedPreparationTime = est_prep_time?.toInt(),
			imageURL = img_url,
			branchID = branch_id,
			specialNotes = special_notes,
		)
	}

	private fun renewMenuItems(newItems: List<MenuItem>) {
		dbQuery.transaction {
			dbQuery.removeAllMenuItems()
			newItems.forEach { item ->
				dbQuery.insertMenuItem(
					item_id = item.id,
					item_name = item.name,
					item_des = item.description,
					price = item.price.toDouble(),
					category_id = item.categoryID,
					is_available = item.isAvailable,
					est_prep_time = item.estimatedPreparationTime?.toString(),
					img_url = item.imageURL,
					branch_id = item.branchID,
					special_notes = item.specialNotes,
				)
			}
		}
	}
}
