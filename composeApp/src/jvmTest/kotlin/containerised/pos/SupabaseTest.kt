package containerised.pos

import containerised.pos.models.Branch
import containerised.pos.models.BranchItem
import containerised.pos.models.Category
import containerised.pos.models.EmployeeShift
import containerised.pos.models.Ingredient
import containerised.pos.models.ItemTag
import containerised.pos.models.Order
import containerised.pos.models.OrderItem
import containerised.pos.models.Promotion
import containerised.pos.models.Role
import containerised.pos.models.SalesReport
import containerised.pos.models.StockAdjustment
import containerised.pos.models.Tag
import containerised.pos.models.User
import containerised.pos.models.UserRole
import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.Test

class SupabaseTest {
	private val missingBranchId = "BRA_TEST_MISSING"
	private val missingCategoryId = "CAT_TEST_MISSING"
	private val missingTagId = "TAG_TEST_MISSING"
	private val missingItemId = "ITEM_TEST_MISSING"
	private val missingIngredientId = "ING_TEST_MISSING"
	private val missingPromotionId = "PRO_TEST_MISSING"
	private val missingOrderId = "ORD_TEST_MISSING"
	private val missingSalesReportId = "REPORT_TEST_MISSING"
	private val missingUuid = "00000000-0000-0000-0000-000000000000"

	@Test
	fun `fetch all collections`() {
		runBlocking {
			val branches = Branch.fetchAll()
			val categories = Category.fetchAll()
			val tags = Tag.fetchAll()
			val salesReports = SalesReport.fetchAll()

			assertNotNull(branches)
			assertNotNull(categories)
			assertNotNull(tags)
			assertNotNull(salesReports)
		}
	}

	@Test
	fun `fetch branch scoped collections`() {
		runBlocking {
			val branchId = Branch.fetchAll().firstOrNull()?.branchId ?: missingBranchId

			val branchItems = BranchItem.fetchByBranch(branchId)
			val detailedBranchItems = BranchItem.fetchByBranchWithDetails(branchId)
			val ingredients = Ingredient.fetchByBranch(branchId)
			val promotions = Promotion.fetchByBranch(branchId)
			val users = User.fetchByBranch(branchId)
			val orders = Order.fetchByBranch(branchId)
			val preparingOrders = Order.fetchPreparingByBranch(branchId)
			val userRolesByBranch = UserRole.fetchAndJoinByBranch(branchId)

			assertTrue(branchItems.all { it.branchId == branchId })
			assertTrue(detailedBranchItems.all { it.branchId == branchId })
			assertTrue(ingredients.all { it.branchId == branchId })
			assertTrue(promotions.all { it.branchId == branchId })
			assertTrue(users.all { it.branchId == branchId })
			assertTrue(orders.all { it.branchId == branchId })
			assertTrue(preparingOrders.all { it.branchId == branchId && it.status == Order.Status.PREPARING })
			assertTrue(userRolesByBranch.all { it.user?.branchId == null || it.user.branchId == branchId })
		}
	}

	@Test
	fun `fetch id based and joined results`() {
		runBlocking {
			val categoryId = Category.fetchAll().firstOrNull()?.categoryId ?: missingCategoryId
			val tagId = Tag.fetchAll().firstOrNull()?.tagId ?: missingTagId
			val branchId = Branch.fetchAll().firstOrNull()?.branchId ?: missingBranchId
			val userId = User.fetchByBranch(branchId).firstOrNull()?.userId ?: missingUuid
			val itemId = BranchItem.fetchByBranch(branchId).firstOrNull()?.itemId ?: missingItemId
			val ingredientId = Ingredient.fetchByBranch(branchId, mustBeActive = false).firstOrNull()?.id ?: missingIngredientId
			val promotionId = Promotion.fetchByBranch(branchId).firstOrNull()?.promotionId ?: missingPromotionId
			val orderId = Order.fetchByBranch(branchId).firstOrNull()?.orderId ?: missingOrderId

			val category = Category.fetchById(categoryId)
			val tag = Tag.fetchById(tagId)
			val user = User.fetchById(userId)
			val branchItem = BranchItem.fetchById(itemId)
			val ingredient = Ingredient.fetchByID(ingredientId)
			val promotion = Promotion.fetchById(promotionId)
			val order = Order.fetchByID(orderId)
			val role = Role.fetchById(missingUuid)
			val userRole = UserRole.fetchAndJoin(userId)
			val userPermissions = UserRole.fetchUserPermission(userId)
			val employeeShifts = EmployeeShift.fetchById(userId)
			val itemTags = ItemTag.fetchByItemId(itemId)
			val orderItems = OrderItem.fetchByOrderWithJoins(orderId)
			val stockAdjustments = StockAdjustment.fetchByIngredientWithDetails(ingredientId)

			if (categoryId == missingCategoryId) assertNull(category) else assertEquals(categoryId, category?.categoryId)
			if (tagId == missingTagId) assertNull(tag) else assertEquals(tagId, tag?.tagId)
			if (userId == missingUuid) assertNull(user) else assertEquals(userId, user?.userId)
			if (itemId == missingItemId) assertNull(branchItem) else assertEquals(itemId, branchItem?.itemId)
			if (ingredientId == missingIngredientId) assertNull(ingredient) else assertEquals(ingredientId, ingredient?.id)
			if (promotionId == missingPromotionId) assertNull(promotion) else assertEquals(promotionId, promotion?.promotionId)
			if (orderId == missingOrderId) assertNull(order) else assertEquals(orderId, order?.orderId)
			assertNull(role)
			if (userId == missingUuid) assertNull(userRole) else assertEquals(userId, userRole?.userId)
			assertNotNull(userPermissions)
			assertTrue(employeeShifts.all { it.userId == userId })
			assertTrue(itemTags.all { it.itemId == itemId })
			assertTrue(orderItems.all { it.orderId == orderId })
			assertTrue(stockAdjustments.all { it.ingredientID == ingredientId })
		}
	}

	@Test
	fun `fetch sales report by id`() {
		runBlocking {
			val reports = SalesReport.fetchAll()

			if (reports.isNotEmpty()) {
				val first = reports.first()
				val fetched = SalesReport.fetchByID(first.id)
				assertEquals(first.id, fetched.id)
			}

			assertFailsWith<Exception> {
				SalesReport.fetchByID(missingSalesReportId)
			}
		}
	}
}
