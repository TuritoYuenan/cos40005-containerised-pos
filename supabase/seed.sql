-- Mock seed data for a small-medium Vietnamese restaurant chain.
-- Insert order follows low-to-high dependency tables.

BEGIN;

-- MARK: Branches
INSERT INTO public.branches (branch_id, branch_name, address, phone_num, email, is_active)
VALUES
	('BRA26032801', 'Pho Diem Tam - District 1', '12 Nguyen Hue, Ben Nghe, District 1, Ho Chi Minh City', '02838223311', 'd1@phodiemtam.vn', true),
	('BRA26032802', 'Pho Diem Tam - Phu Nhuan', '88 Phan Xich Long, Ward 2, Phu Nhuan, Ho Chi Minh City', '02839995544', 'pn@phodiemtam.vn', true),
	('BRA26032803', 'Pho Diem Tam - Da Nang', '45 Bach Dang, Hai Chau 1, Hai Chau, Da Nang', '02363888999', 'dn@phodiemtam.vn', true)
ON CONFLICT (branch_id) DO NOTHING;

-- MARK: Categories
INSERT INTO public.categories (category_id, category_name, display_order)
VALUES
	('CAT26032801', 'Pho', 1),
	('CAT26032802', 'Bun Noodle Soup', 2),
	('CAT26032803', 'Com Rice Dishes', 3),
	('CAT26032804', 'Appetizers', 4),
	('CAT26032805', 'Grilled & BBQ', 5),
	('CAT26032806', 'Vegetarian', 6),
	('CAT26032807', 'Vietnamese Coffee & Tea', 7),
	('CAT26032808', 'Desserts', 8)
ON CONFLICT (category_id) DO NOTHING;

-- MARK: Tags
INSERT INTO public.tags (tag_id, tag_name, tag_desc)
VALUES
	('TAG26032801', 'Best Seller', 'Popular item among regular customers'),
	('TAG26032802', 'Chef Recommendation', 'Highlighted by the head chef'),
	('TAG26032803', 'Spicy', 'Contains chili or spicy condiments'),
	('TAG26032804', 'Vegetarian', 'No meat ingredients'),
	('TAG26032805', 'Signature', 'House signature item'),
	('TAG26032806', 'New', 'Recently added to the menu'),
	('TAG26032807', 'Low Sugar', 'Reduced sugar recipe'),
	('TAG26032808', 'Iced', 'Served chilled with ice'),
	('TAG26032809', 'Hot Favorite', 'Commonly ordered as hot beverage'),
	('TAG26032810', 'Family Size', 'Suitable for sharing')
ON CONFLICT (tag_id) DO NOTHING;

-- MARK: Sreports
INSERT INTO public.sales_reports (
	id, granularity, period_start, period_end, currency,
	gross_sales, discount_total, net_sales, orders_count, source_job
)
VALUES
	('SRP26032801', 'DAILY', '2026-03-21', '2026-03-21', 'VND', 18500000, 850000, 17650000, 126, 'manual-seed'),
	('SRP26032802', 'DAILY', '2026-03-22', '2026-03-22', 'VND', 17200000, 620000, 16580000, 118, 'manual-seed'),
	('SRP26032803', 'DAILY', '2026-03-23', '2026-03-23', 'VND', 19450000, 970000, 18480000, 133, 'manual-seed'),
	('SRP26032804', 'WEEKLY', '2026-03-17', '2026-03-23', 'VND', 126400000, 6420000, 119980000, 861, 'manual-seed'),
	('SRP26032805', 'MONTHLY', '2026-02-01', '2026-02-28', 'VND', 486000000, 23700000, 462300000, 3340, 'manual-seed'),
	('SRP26032806', 'MONTHLY', '2026-03-01', '2026-03-27', 'VND', 514800000, 26100000, 488700000, 3526, 'manual-seed'),
	('SRP26032807', 'YEARLY', '2025-01-01', '2025-12-31', 'VND', 4860000000, 243000000, 4617000000, 32780, 'manual-seed')
ON CONFLICT (id) DO NOTHING;

-- MARK: Tables
INSERT INTO public.tables (table_id, branch_id, table_code, is_active)
VALUES
	('TAB26032801', 'BRA26032801', 'D1-T01', true),
	('TAB26032802', 'BRA26032801', 'D1-T02', true),
	('TAB26032803', 'BRA26032801', 'D1-T03', true),
	('TAB26032804', 'BRA26032801', 'D1-T04', true),
	('TAB26032805', 'BRA26032802', 'PN-T01', true),
	('TAB26032806', 'BRA26032802', 'PN-T02', true),
	('TAB26032807', 'BRA26032802', 'PN-T03', true),
	('TAB26032808', 'BRA26032802', 'PN-T04', true),
	('TAB26032809', 'BRA26032803', 'DN-T01', true),
	('TAB26032810', 'BRA26032803', 'DN-T02', true),
	('TAB26032811', 'BRA26032803', 'DN-T03', true),
	('TAB26032812', 'BRA26032803', 'DN-T04', true)
ON CONFLICT (table_id) DO NOTHING;

-- MARK: Ingredients
INSERT INTO public.ingredients (
	ingredient_id, ingredient_name, unit, current_stock, min_stock_level,
	supplier_info, branch_id, is_active
)
VALUES
	('ING26032801', 'Pho rice noodles', 'kg', 42, 15, '{"supplier": "Saigon Noodle Mill", "contact": "0909000111"}'::jsonb, 'BRA26032801', true),
	('ING26032802', 'Beef brisket', 'kg', 35, 12, '{"supplier": "An Phu Meat", "contact": "0909000222"}'::jsonb, 'BRA26032801', true),
	('ING26032803', 'Beef balls', 'kg', 22, 8, '{"supplier": "An Phu Meat", "contact": "0909000222"}'::jsonb, 'BRA26032801', true),
	('ING26032804', 'Chicken thighs', 'kg', 27, 10, '{"supplier": "Viet Farm", "contact": "0909000333"}'::jsonb, 'BRA26032801', true),
	('ING26032805', 'Lemongrass', 'kg', 12, 4, '{"supplier": "Cho Dau Moi Thu Duc", "contact": "0909000444"}'::jsonb, 'BRA26032801', true),
	('ING26032806', 'Fish sauce', 'litre', 18, 6, '{"supplier": "Phu Quoc Barrel", "contact": "0909000555"}'::jsonb, 'BRA26032801', true),
	('ING26032807', 'Condensed milk', 'can', 58, 20, '{"supplier": "Vinamilk Distributor", "contact": "0909000666"}'::jsonb, 'BRA26032801', true),
	('ING26032808', 'Robusta coffee beans', 'kg', 16, 5, '{"supplier": "Buon Ma Thuot Coffee", "contact": "0909000777"}'::jsonb, 'BRA26032801', true),
	('ING26032809', 'Jasmine tea leaves', 'kg', 8, 3, '{"supplier": "Bao Loc Tea", "contact": "0909000888"}'::jsonb, 'BRA26032802', true),
	('ING26032810', 'Pork belly', 'kg', 31, 10, '{"supplier": "Tan Binh Market", "contact": "0909000999"}'::jsonb, 'BRA26032802', true),
	('ING26032811', 'Shrimp', 'kg', 24, 8, '{"supplier": "Nha Be Seafood", "contact": "0909001001"}'::jsonb, 'BRA26032802', true),
	('ING26032812', 'Fresh herbs mix', 'kg', 14, 5, '{"supplier": "Da Lat Greens", "contact": "0909001002"}'::jsonb, 'BRA26032802', true),
	('ING26032813', 'Tofu', 'block', 48, 16, '{"supplier": "Saigon Tofu", "contact": "0909001003"}'::jsonb, 'BRA26032803', true),
	('ING26032814', 'Rice paper', 'pack', 36, 12, '{"supplier": "Mekong Ricecraft", "contact": "0909001004"}'::jsonb, 'BRA26032803', true),
	('ING26032815', 'Coconut milk', 'litre', 20, 7, '{"supplier": "Ben Tre Coconut", "contact": "0909001005"}'::jsonb, 'BRA26032803', true)
ON CONFLICT (ingredient_id) DO NOTHING;

-- MARK: Promotions
-- days_of_week example: "[\n {\n \"day\": \"MONDAY\",\n \"startTime\": \"09:00\",\n \"endTime\": \"22:00\"\n }\n]"
-- rules example: "[\n {\n \"targetType\": \"ITEM\",\n \"selectedIds\": [\n \"ITM26011701\"\n ],\n \"rewardType\": \"GIFT\",\n \"rewardItems\": {\n \"ITM26011701\": 2,\n \"ITM26011703\": 3\n }\n }\n]"
INSERT INTO public.promotions (
	promotion_id, branch_id,
	start_date, end_date, is_active
)
VALUES
	('PRO26032801', 'BRA26032801', '2026-03-25', '2026-03-31', true),
	('PRO26032802', 'BRA26032801', '2026-03-28', '2026-04-04', true),
	('PRO26032803', 'BRA26032802', '2026-03-30', '2026-04-05', true),
	('PRO26032804', 'BRA26032802', '2026-03-27', '2026-03-29', true),
	('PRO26032805', 'BRA26032803', '2026-03-26', '2026-04-01', true),
	('PRO26032806', 'BRA26032803', '2026-03-28', '2026-04-04', true)
ON CONFLICT (promotion_id) DO NOTHING;

-- MARK: Bitems
INSERT INTO public.branch_items (
	item_id, branch_id, category_id, item_name, item_desc, price,
	estimated_prep, is_available, is_featured, url_img
)
VALUES
	('ITM26032801', 'BRA26032801', 'CAT26032801', 'Pho Bo Tai', 'Beef pho with sliced rare beef and herbs', 85000, '12 min', true, true, 'https://placehold.co/160x90'),
	('ITM26032802', 'BRA26032801', 'CAT26032801', 'Pho Bo Vien', 'Beef pho with springy beef balls', 82000, '12 min', true, false, 'https://placehold.co/160x90'),
	('ITM26032803', 'BRA26032801', 'CAT26032802', 'Bun Bo Hue', 'Hue spicy beef noodle soup', 90000, '15 min', true, true, 'https://placehold.co/160x90'),
	('ITM26032804', 'BRA26032801', 'CAT26032804', 'Goi Cuon Tom Thit', 'Fresh spring rolls with shrimp and pork', 68000, '10 min', true, false, 'https://placehold.co/160x90'),
	('ITM26032805', 'BRA26032801', 'CAT26032807', 'Ca Phe Sua Da', 'Vietnamese iced milk coffee', 42000, '5 min', true, true, 'https://placehold.co/160x90'),
	('ITM26032806', 'BRA26032801', 'CAT26032807', 'Tra Dao Cam Sa', 'Peach lemongrass tea', 48000, '6 min', true, false, 'https://placehold.co/160x90'),
	('ITM26032807', 'BRA26032802', 'CAT26032803', 'Com Tam Suon Bi Cha', 'Broken rice with grilled pork chop, shredded pork and egg meatloaf', 92000, '14 min', true, true, 'https://placehold.co/160x90'),
	('ITM26032808', 'BRA26032802', 'CAT26032805', 'Bun Thit Nuong', 'Grilled pork vermicelli bowl with fish sauce dressing', 86000, '13 min', true, false, 'https://placehold.co/160x90'),
	('ITM26032809', 'BRA26032802', 'CAT26032804', 'Cha Gio Hai San', 'Crispy seafood spring rolls', 74000, '11 min', true, false, 'https://placehold.co/160x90'),
	('ITM26032810', 'BRA26032802', 'CAT26032806', 'Pho Chay Nam', 'Vegetarian pho with mushroom broth', 78000, '12 min', true, false, 'https://placehold.co/160x90'),
	('ITM26032811', 'BRA26032802', 'CAT26032807', 'Bac Xiu Nong', 'Hot bac xiu with rich milk foam', 45000, '6 min', true, false, 'https://placehold.co/160x90'),
	('ITM26032812', 'BRA26032802', 'CAT26032808', 'Che Ba Mau', 'Three-color sweet dessert', 39000, '5 min', true, false, 'https://placehold.co/160x90'),
	('ITM26032813', 'BRA26032803', 'CAT26032802', 'Mi Quang Ga', 'Quang noodles with chicken and peanuts', 88000, '14 min', true, true, 'https://placehold.co/160x90'),
	('ITM26032814', 'BRA26032803', 'CAT26032805', 'Banh Xeo Mien Trung', 'Central style crispy banh xeo with herbs', 89000, '16 min', true, true, 'https://placehold.co/160x90'),
	('ITM26032815', 'BRA26032803', 'CAT26032803', 'Com Ga Hoi An', 'Hoi An chicken rice with turmeric', 93000, '15 min', true, false, 'https://placehold.co/160x90'),
	('ITM26032816', 'BRA26032803', 'CAT26032806', 'Dau Hu Sot Nam Dong Co', 'Tofu with mushroom sauce and seasonal greens', 76000, '10 min', true, false, 'https://placehold.co/160x90'),
	('ITM26032817', 'BRA26032803', 'CAT26032807', 'Ca Phe Den Nong', 'Hot black Vietnamese drip coffee', 38000, '6 min', true, false, 'https://placehold.co/160x90'),
	('ITM26032818', 'BRA26032803', 'CAT26032808', 'Banh Flan Ca Phe', 'Coffee caramel flan', 42000, '4 min', true, false, 'https://placehold.co/160x90')
ON CONFLICT (item_id) DO NOTHING;

-- MARK: Pconditions
INSERT INTO public.promotion_conditions (condition_id, promotion_id, condition_type, value)
VALUES
	('PCN26032801', 'PRM26032801', 'minimum_spend', '{"amount": 120000, "currency": "VND"}'::jsonb),
	('PCN26032802', 'PRM26032801', 'time_window', '{"start": "11:00", "end": "14:00"}'::jsonb),
	('PCN26032803', 'PRM26032802', 'minimum_spend', '{"amount": 150000, "currency": "VND"}'::jsonb),
	('PCN26032804', 'PRM26032803', 'weekday_only', '{"weekday": "TUESDAY"}'::jsonb),
	('PCN26032805', 'PRM26032803', 'requires_student_id', '{"required": true}'::jsonb),
	('PCN26032806', 'PRM26032804', 'party_size', '{"min_people": 4}'::jsonb),
	('PCN26032807', 'PRM26032806', 'buy_x_get_y', '{"buy": 2, "free": 1, "category": "coffee"}'::jsonb),
	('PCN26032808', 'PRM26032806', 'time_window', '{"start": "15:00", "end": "17:00"}'::jsonb)
ON CONFLICT (condition_id) DO NOTHING;

-- MARK: Item ingredients
INSERT INTO public.item_ingredients (ingredient_id, item_id, quantity, unit)
VALUES
	('ING26032801', 'ITM26032801', 0.18, 'kg'),
	('ING26032802', 'ITM26032801', 0.12, 'kg'),
	('ING26032801', 'ITM26032802', 0.18, 'kg'),
	('ING26032803', 'ITM26032802', 0.10, 'kg'),
	('ING26032802', 'ITM26032803', 0.10, 'kg'),
	('ING26032805', 'ITM26032803', 0.03, 'kg'),
	('ING26032811', 'ITM26032804', 0.08, 'kg'),
	('ING26032810', 'ITM26032804', 0.06, 'kg'),
	('ING26032807', 'ITM26032805', 0.25, 'can'),
	('ING26032808', 'ITM26032805', 0.02, 'kg'),
	('ING26032809', 'ITM26032806', 0.01, 'kg'),
	('ING26032805', 'ITM26032806', 0.02, 'kg'),
	('ING26032810', 'ITM26032807', 0.16, 'kg'),
	('ING26032806', 'ITM26032807', 0.01, 'litre'),
	('ING26032810', 'ITM26032808', 0.14, 'kg'),
	('ING26032806', 'ITM26032808', 0.01, 'litre'),
	('ING26032813', 'ITM26032810', 1.00, 'block'),
	('ING26032804', 'ITM26032813', 0.15, 'kg'),
	('ING26032814', 'ITM26032814', 0.20, 'pack'),
	('ING26032815', 'ITM26032818', 0.05, 'litre')
ON CONFLICT (ingredient_id, item_id) DO NOTHING;

-- MARK: Item tags
DO $$
BEGIN
	IF to_regclass('public.item_tags') IS NOT NULL THEN
		INSERT INTO public.item_tags (item_id, tag_id)
		VALUES
			('ITM26032801', 'TAG26032801'),
			('ITM26032801', 'TAG26032805'),
			('ITM26032803', 'TAG26032802'),
			('ITM26032803', 'TAG26032803'),
			('ITM26032805', 'TAG26032801'),
			('ITM26032805', 'TAG26032808'),
			('ITM26032806', 'TAG26032806'),
			('ITM26032807', 'TAG26032801'),
			('ITM26032808', 'TAG26032805'),
			('ITM26032809', 'TAG26032810'),
			('ITM26032810', 'TAG26032804'),
			('ITM26032811', 'TAG26032809'),
			('ITM26032812', 'TAG26032807'),
			('ITM26032813', 'TAG26032802'),
			('ITM26032814', 'TAG26032805'),
			('ITM26032815', 'TAG26032810'),
			('ITM26032816', 'TAG26032804'),
			('ITM26032817', 'TAG26032809'),
			('ITM26032818', 'TAG26032806'),
			('ITM26032818', 'TAG26032807')
		ON CONFLICT DO NOTHING;
	ELSIF to_regclass('public.branch_item_tags') IS NOT NULL THEN
		INSERT INTO public.branch_item_tags (item_id, tag_id)
		VALUES
			('ITM26032801', 'TAG26032801'),
			('ITM26032801', 'TAG26032805'),
			('ITM26032803', 'TAG26032802'),
			('ITM26032803', 'TAG26032803'),
			('ITM26032805', 'TAG26032801'),
			('ITM26032805', 'TAG26032808'),
			('ITM26032806', 'TAG26032806'),
			('ITM26032807', 'TAG26032801'),
			('ITM26032808', 'TAG26032805'),
			('ITM26032809', 'TAG26032810'),
			('ITM26032810', 'TAG26032804'),
			('ITM26032811', 'TAG26032809'),
			('ITM26032812', 'TAG26032807'),
			('ITM26032813', 'TAG26032802'),
			('ITM26032814', 'TAG26032805'),
			('ITM26032815', 'TAG26032810'),
			('ITM26032816', 'TAG26032804'),
			('ITM26032817', 'TAG26032809'),
			('ITM26032818', 'TAG26032806'),
			('ITM26032818', 'TAG26032807')
		ON CONFLICT DO NOTHING;
	END IF;
END $$;

-- MARK: Orders
INSERT INTO public.orders (
	order_id, order_number, order_type, table_id, status,
	branch_id, tax_amount, final_amount, updated_at, created_at
)
VALUES
	('ORD26032801', '001', 'DINE_IN', 'TAB26032801', 'FINISHED', 'BRA26032801', 12000, 132000, '2026-03-28 11:35:00', '2026-03-28 11:10:00+07'),
	('ORD26032802', '002', 'DINE_IN', 'TAB26032802', 'PREPARING', 'BRA26032801', 9000, 99000, '2026-03-28 12:05:00', '2026-03-28 11:52:00+07'),
	('ORD26032803', '003', 'TAKEAWAY', NULL, 'FINISHED', 'BRA26032801', 8000, 88000, '2026-03-28 12:20:00', '2026-03-28 12:00:00+07'),
	('ORD26032804', '001', 'DINE_IN', 'TAB26032805', 'FINISHED', 'BRA26032802', 15000, 165000, '2026-03-28 11:48:00', '2026-03-28 11:15:00+07'),
	('ORD26032805', '002', 'DINE_IN', 'TAB26032806', 'PREPARING', 'BRA26032802', 11000, 121000, '2026-03-28 12:10:00', '2026-03-28 11:58:00+07'),
	('ORD26032806', '003', 'DELIVERY', NULL, 'FINISHED', 'BRA26032802', 7000, 77000, '2026-03-28 12:22:00', '2026-03-28 12:05:00+07'),
	('ORD26032807', '001', 'DINE_IN', 'TAB26032809', 'FINISHED', 'BRA26032803', 13000, 143000, '2026-03-28 11:50:00', '2026-03-28 11:18:00+07'),
	('ORD26032808', '002', 'DINE_IN', 'TAB26032810', 'PREPARING', 'BRA26032803', 10000, 110000, '2026-03-28 12:08:00', '2026-03-28 11:56:00+07'),
	('ORD26032809', '003', 'TAKEAWAY', NULL, 'CANCELED', 'BRA26032803', 0, 0, '2026-03-28 12:12:00', '2026-03-28 12:01:00+07'),
	('ORD26032810', '004', 'DELIVERY', NULL, 'FINISHED', 'BRA26032801', 14000, 154000, '2026-03-28 12:32:00', '2026-03-28 12:10:00+07'),
	('ORD26032811', '004', 'TAKEAWAY', NULL, 'FINISHED', 'BRA26032802', 6000, 66000, '2026-03-28 12:28:00', '2026-03-28 12:12:00+07'),
	('ORD26032812', '004', 'DINE_IN', 'TAB26032811', 'PREPARING', 'BRA26032803', 9000, 99000, '2026-03-28 12:30:00', '2026-03-28 12:14:00+07')
ON CONFLICT (order_id) DO NOTHING;

-- MARK: Order items
INSERT INTO public.order_items (
	order_id, item_id, quantity, subtotal, special_notes, item_status
)
VALUES
	('ORD26032801', 'ITM26032801', 1, 85000, 'Less onion', 'FINISHED'),
	('ORD26032801', 'ITM26032805', 1, 42000, NULL, 'FINISHED'),
	('ORD26032802', 'ITM26032803', 1, 90000, 'Extra spicy', 'PREPARING'),
	('ORD26032802', 'ITM26032806', 1, 48000, 'Less sugar', 'PREPARING'),
	('ORD26032803', 'ITM26032802', 1, 82000, NULL, 'FINISHED'),
	('ORD26032804', 'ITM26032807', 1, 92000, 'No cucumber', 'FINISHED'),
	('ORD26032804', 'ITM26032812', 1, 39000, NULL, 'FINISHED'),
	('ORD26032804', 'ITM26032811', 1, 45000, NULL, 'FINISHED'),
	('ORD26032805', 'ITM26032808', 1, 86000, 'More fish sauce', 'PREPARING'),
	('ORD26032805', 'ITM26032809', 1, 74000, NULL, 'PREPARING'),
	('ORD26032806', 'ITM26032810', 1, 78000, NULL, 'FINISHED'),
	('ORD26032807', 'ITM26032813', 1, 88000, NULL, 'FINISHED'),
	('ORD26032807', 'ITM26032817', 1, 38000, 'No sugar', 'FINISHED'),
	('ORD26032808', 'ITM26032814', 1, 89000, NULL, 'PREPARING'),
	('ORD26032808', 'ITM26032818', 1, 42000, NULL, 'PREPARING'),
	('ORD26032809', 'ITM26032815', 1, 93000, NULL, 'CANCELED'),
	('ORD26032810', 'ITM26032801', 1, 85000, NULL, 'FINISHED'),
	('ORD26032810', 'ITM26032804', 1, 68000, NULL, 'FINISHED'),
	('ORD26032811', 'ITM26032812', 1, 39000, NULL, 'FINISHED'),
	('ORD26032812', 'ITM26032816', 1, 76000, 'No spring onion', 'PREPARING')
ON CONFLICT (order_id, item_id) DO NOTHING;

-- MARK: Stock adjustments
INSERT INTO public.stock_adjustments (
	record_id, staff_id, ingredient_id, adjustment_type,
	quantity_before, quantity_after, timestamp, notes
)
VALUES
	('STA26032801', '85145a46-cd34-4c7c-9708-7d82f6c87ce3', 'ING26032801', 'DECREMENT', 45, 42, '2026-03-28 10:30:00+07', 'Used 3 kg in order ORD26032701'),
	('STA26032802', '85145a46-cd34-4c7c-9708-7d82f6c87ce3', 'ING26032802', 'DECREMENT', 38, 35, '2026-03-28 10:35:00+07', 'Used 3 kg in order ORD26032702'),
	('STA26032803', '85145a46-cd34-4c7c-9708-7d82f6c87ce3', 'ING26032803', 'DECREMENT', 24, 22, '2026-03-28 10:40:00+07', 'Used 2 kg in order ORD26032703'),
	('STA26032804', '85145a46-cd34-4c7c-9708-7d82f6c87ce3', 'ING26032804', 'DECREMENT', 30, 27, '2026-03-28 10:42:00+07', 'Used 3 kg in order ORD26032704'),
	('STA26032805', '85145a46-cd34-4c7c-9708-7d82f6c87ce3', 'ING26032805', 'DECREMENT', 14, 12, '2026-03-28 10:45:00+07', 'Used 2 kg in order ORD26032705'),
	('STA26032806', '85145a46-cd34-4c7c-9708-7d82f6c87ce3', 'ING26032807', 'DECREMENT', 60, 58, '2026-03-28 11:00:00+07', 'Used 2 can in order ORD26032706'),
	('STA26032807', '85145a46-cd34-4c7c-9708-7d82f6c87ce3', 'ING26032808', 'DECREMENT', 17, 16, '2026-03-28 11:02:00+07', 'Used 1 kg in order ORD26032707'),
	('STA26032808', '85145a46-cd34-4c7c-9708-7d82f6c87ce3', 'ING26032809', 'DECREMENT',  9,  8, '2026-03-28 11:05:00+07', 'Used 1 kg in order ORD26032708'),
	('STA26032809', '85145a46-cd34-4c7c-9708-7d82f6c87ce3', 'ING26032810', 'DECREMENT', 33, 31, '2026-03-28 11:08:00+07', 'Used 2 kg in order ORD26032709'),
	('STA26032810', '85145a46-cd34-4c7c-9708-7d82f6c87ce3', 'ING26032811', 'DECREMENT', 26, 24, '2026-03-28 11:10:00+07', 'Used 2 kg in order ORD26032710'),
	('STA26032811', '85145a46-cd34-4c7c-9708-7d82f6c87ce3', 'ING26032813', 'DECREMENT', 50, 48, '2026-03-28 11:14:00+07', 'Used 2 block in order ORD26032711'),
	('STA26032812', '85145a46-cd34-4c7c-9708-7d82f6c87ce3', 'ING26032815', 'DECREMENT', 22, 20, '2026-03-28 11:16:00+07', 'Used 2 litre in order ORD26032712')
ON CONFLICT (record_id) DO NOTHING;

COMMIT;
