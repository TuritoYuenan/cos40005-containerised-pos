export type Json =
	| string
	| number
	| boolean
	| null
	| { [key: string]: Json | undefined }
	| Json[];

export type Database = {
	// Allows to automatically instantiate createClient with right options
	// instead of createClient<Database, { PostgrestVersion: 'XX' }>(URL, KEY)
	__InternalSupabase: {
		PostgrestVersion: "13.0.5";
	};
	public: {
		Tables: {
			branch_items: {
				Row: {
					branch_id: string;
					category_id: string | null;
					estimated_prep: string;
					is_available: boolean;
					is_featured: boolean;
					item_desc: string | null;
					item_id: string;
					item_name: string;
					price: number;
					url_img: string | null;
				};
				Insert: {
					branch_id: string;
					category_id?: string | null;
					estimated_prep: string;
					is_available?: boolean;
					is_featured?: boolean;
					item_desc?: string | null;
					item_id?: string;
					item_name: string;
					price: number;
					url_img?: string | null;
				};
				Update: {
					branch_id?: string;
					category_id?: string | null;
					estimated_prep?: string;
					is_available?: boolean;
					is_featured?: boolean;
					item_desc?: string | null;
					item_id?: string;
					item_name?: string;
					price?: number;
					url_img?: string | null;
				};
				Relationships: [
					{
						foreignKeyName: "branch_items_branch_id_fkey";
						columns: ["branch_id"];
						isOneToOne: false;
						referencedRelation: "branches";
						referencedColumns: ["branch_id"];
					},
					{
						foreignKeyName: "branch_items_category_id_fkey";
						columns: ["category_id"];
						isOneToOne: false;
						referencedRelation: "categories";
						referencedColumns: ["category_id"];
					},
				];
			};
			branches: {
				Row: {
					address: string;
					branch_id: string;
					branch_name: string;
					created_at: string;
					email: string;
					is_active: boolean;
					phone_num: string;
				};
				Insert: {
					address: string;
					branch_id?: string;
					branch_name: string;
					created_at?: string;
					email: string;
					is_active?: boolean;
					phone_num: string;
				};
				Update: {
					address?: string;
					branch_id?: string;
					branch_name?: string;
					created_at?: string;
					email?: string;
					is_active?: boolean;
					phone_num?: string;
				};
				Relationships: [];
			};
			categories: {
				Row: {
					category_id: string;
					category_name: string;
					display_order: number;
				};
				Insert: {
					category_id?: string;
					category_name: string;
					display_order?: number;
				};
				Update: {
					category_id?: string;
					category_name?: string;
					display_order?: number;
				};
				Relationships: [];
			};
			employee_shifts: {
				Row: {
					date: Database["public"]["Enums"]["DOTW"];
					end_time: string;
					start_time: string;
					user_id: string;
				};
				Insert: {
					date: Database["public"]["Enums"]["DOTW"];
					end_time: string;
					start_time: string;
					user_id: string;
				};
				Update: {
					date?: Database["public"]["Enums"]["DOTW"];
					end_time?: string;
					start_time?: string;
					user_id?: string;
				};
				Relationships: [
					{
						foreignKeyName: "employee_shifts_user_id_fkey";
						columns: ["user_id"];
						isOneToOne: false;
						referencedRelation: "users";
						referencedColumns: ["user_id"];
					},
				];
			};
			ingredients: {
				Row: {
					branch_id: string | null;
					current_stock: number | null;
					ingredient_id: string;
					ingredient_name: string | null;
					is_active: boolean | null;
					min_stock_level: number | null;
					supplier_info: Json | null;
					unit: string | null;
				};
				Insert: {
					branch_id?: string | null;
					current_stock?: number | null;
					ingredient_id?: string;
					ingredient_name?: string | null;
					is_active?: boolean | null;
					min_stock_level?: number | null;
					supplier_info?: Json | null;
					unit?: string | null;
				};
				Update: {
					branch_id?: string | null;
					current_stock?: number | null;
					ingredient_id?: string;
					ingredient_name?: string | null;
					is_active?: boolean | null;
					min_stock_level?: number | null;
					supplier_info?: Json | null;
					unit?: string | null;
				};
				Relationships: [
					{
						foreignKeyName: "ingredients_branch_id_fkey";
						columns: ["branch_id"];
						isOneToOne: false;
						referencedRelation: "branches";
						referencedColumns: ["branch_id"];
					},
				];
			};
			item_ingredients: {
				Row: {
					ingredient_id: string;
					item_id: string;
					quantity: number | null;
					unit: string | null;
				};
				Insert: {
					ingredient_id: string;
					item_id: string;
					quantity?: number | null;
					unit?: string | null;
				};
				Update: {
					ingredient_id?: string;
					item_id?: string;
					quantity?: number | null;
					unit?: string | null;
				};
				Relationships: [
					{
						foreignKeyName: "item_ingredients_ingredient_id_fkey";
						columns: ["ingredient_id"];
						isOneToOne: false;
						referencedRelation: "ingredients";
						referencedColumns: ["ingredient_id"];
					},
					{
						foreignKeyName: "item_ingredients_item_id_fkey";
						columns: ["item_id"];
						isOneToOne: false;
						referencedRelation: "branch_items";
						referencedColumns: ["item_id"];
					},
				];
			};
			item_tags: {
				Row: {
					item_id: string;
					tag_id: string;
				};
				Insert: {
					item_id: string;
					tag_id: string;
				};
				Update: {
					item_id?: string;
					tag_id?: string;
				};
				Relationships: [
					{
						foreignKeyName: "branch_item_tags_tag_id_fkey";
						columns: ["tag_id"];
						isOneToOne: false;
						referencedRelation: "tags";
						referencedColumns: ["tag_id"];
					},
					{
						foreignKeyName: "item_tags_item_id_fkey";
						columns: ["item_id"];
						isOneToOne: false;
						referencedRelation: "branch_items";
						referencedColumns: ["item_id"];
					},
				];
			};
			order_items: {
				Row: {
					item_id: string;
					item_status: Database["public"]["Enums"]["status"];
					order_id: string;
					quantity: number;
					special_notes: string | null;
					subtotal: number | null;
				};
				Insert: {
					item_id: string;
					item_status?: Database["public"]["Enums"]["status"];
					order_id: string;
					quantity: number;
					special_notes?: string | null;
					subtotal?: number | null;
				};
				Update: {
					item_id?: string;
					item_status?: Database["public"]["Enums"]["status"];
					order_id?: string;
					quantity?: number;
					special_notes?: string | null;
					subtotal?: number | null;
				};
				Relationships: [
					{
						foreignKeyName: "order_items_item_id_fkey";
						columns: ["item_id"];
						isOneToOne: false;
						referencedRelation: "branch_items";
						referencedColumns: ["item_id"];
					},
					{
						foreignKeyName: "order_items_order_id_fkey";
						columns: ["order_id"];
						isOneToOne: false;
						referencedRelation: "orders";
						referencedColumns: ["order_id"];
					},
				];
			};
			orders: {
				Row: {
					branch_id: string | null;
					created_at: string;
					final_amount: number | null;
					order_id: string;
					order_number: string | null;
					order_type: string | null;
					status: Database["public"]["Enums"]["status"];
					table_id: string | null;
					tax_amount: number | null;
					updated_at: string | null;
				};
				Insert: {
					branch_id?: string | null;
					created_at?: string;
					final_amount?: number | null;
					order_id?: string;
					order_number?: string | null;
					order_type?: string | null;
					status?: Database["public"]["Enums"]["status"];
					table_id?: string | null;
					tax_amount?: number | null;
					updated_at?: string | null;
				};
				Update: {
					branch_id?: string | null;
					created_at?: string;
					final_amount?: number | null;
					order_id?: string;
					order_number?: string | null;
					order_type?: string | null;
					status?: Database["public"]["Enums"]["status"];
					table_id?: string | null;
					tax_amount?: number | null;
					updated_at?: string | null;
				};
				Relationships: [
					{
						foreignKeyName: "orders_branch_id_fkey";
						columns: ["branch_id"];
						isOneToOne: false;
						referencedRelation: "branches";
						referencedColumns: ["branch_id"];
					},
					{
						foreignKeyName: "orders_table_id_fkey";
						columns: ["table_id"];
						isOneToOne: false;
						referencedRelation: "tables";
						referencedColumns: ["table_id"];
					},
				];
			};
			promotion_conditions: {
				Row: {
					condition_id: string;
					condition_type: string;
					promotion_id: string;
					value: Json;
				};
				Insert: {
					condition_id: string;
					condition_type: string;
					promotion_id: string;
					value: Json;
				};
				Update: {
					condition_id?: string;
					condition_type?: string;
					promotion_id?: string;
					value?: Json;
				};
				Relationships: [];
			};
			promotions: {
				Row: {
					branch_id: string;
					days_of_week: Json | null;
					end_date: string | null;
					is_active: boolean | null;
					promotion_id: string;
					rules: Json | null;
					start_date: string | null;
				};
				Insert: {
					branch_id: string;
					days_of_week?: Json | null;
					end_date?: string | null;
					is_active?: boolean | null;
					promotion_id?: string;
					rules?: Json | null;
					start_date?: string | null;
				};
				Update: {
					branch_id?: string;
					days_of_week?: Json | null;
					end_date?: string | null;
					is_active?: boolean | null;
					promotion_id?: string;
					rules?: Json | null;
					start_date?: string | null;
				};
				Relationships: [
					{
						foreignKeyName: "promotions_branch_id_fkey";
						columns: ["branch_id"];
						isOneToOne: false;
						referencedRelation: "branches";
						referencedColumns: ["branch_id"];
					},
				];
			};
			roles: {
				Row: {
					description: string | null;
					permission: Json | null;
					role_id: string;
					role_name: string;
				};
				Insert: {
					description?: string | null;
					permission?: Json | null;
					role_id?: string;
					role_name: string;
				};
				Update: {
					description?: string | null;
					permission?: Json | null;
					role_id?: string;
					role_name?: string;
				};
				Relationships: [];
			};
			sales_reports: {
				Row: {
					currency: string;
					discount_total: number;
					generated_at: string;
					granularity:
						Database["public"]["Enums"]["sales_report_granularity"];
					gross_sales: number;
					id: string;
					net_sales: number;
					orders_count: number;
					period_end: string;
					period_start: string;
					source_job: string | null;
				};
				Insert: {
					currency?: string;
					discount_total?: number;
					generated_at?: string;
					granularity:
						Database["public"]["Enums"]["sales_report_granularity"];
					gross_sales?: number;
					id?: string;
					net_sales?: number;
					orders_count?: number;
					period_end: string;
					period_start: string;
					source_job?: string | null;
				};
				Update: {
					currency?: string;
					discount_total?: number;
					generated_at?: string;
					granularity?:
						Database["public"]["Enums"]["sales_report_granularity"];
					gross_sales?: number;
					id?: string;
					net_sales?: number;
					orders_count?: number;
					period_end?: string;
					period_start?: string;
					source_job?: string | null;
				};
				Relationships: [];
			};
			stock_adjustments: {
				Row: {
					adjustment_type:
						Database["public"]["Enums"]["adjustment_type"];
					ingredient_id: string;
					notes: string | null;
					quantity_after: number;
					quantity_before: number;
					record_id: string;
					staff_id: string | null;
					timestamp: string;
				};
				Insert: {
					adjustment_type:
						Database["public"]["Enums"]["adjustment_type"];
					ingredient_id: string;
					notes?: string | null;
					quantity_after: number;
					quantity_before: number;
					record_id?: string;
					staff_id?: string | null;
					timestamp?: string;
				};
				Update: {
					adjustment_type?:
						Database["public"]["Enums"]["adjustment_type"];
					ingredient_id?: string;
					notes?: string | null;
					quantity_after?: number;
					quantity_before?: number;
					record_id?: string;
					staff_id?: string | null;
					timestamp?: string;
				};
				Relationships: [
					{
						foreignKeyName: "stock_adjustments_ingredient_id_fkey";
						columns: ["ingredient_id"];
						isOneToOne: false;
						referencedRelation: "ingredients";
						referencedColumns: ["ingredient_id"];
					},
					{
						foreignKeyName: "stock_adjustments_staff_id_fkey1";
						columns: ["staff_id"];
						isOneToOne: false;
						referencedRelation: "users";
						referencedColumns: ["user_id"];
					},
				];
			};
			tables: {
				Row: {
					branch_id: string;
					is_active: boolean;
					table_code: string;
					table_id: string;
				};
				Insert: {
					branch_id: string;
					is_active?: boolean;
					table_code: string;
					table_id?: string;
				};
				Update: {
					branch_id?: string;
					is_active?: boolean;
					table_code?: string;
					table_id?: string;
				};
				Relationships: [
					{
						foreignKeyName: "tables_branch_id_fkey";
						columns: ["branch_id"];
						isOneToOne: false;
						referencedRelation: "branches";
						referencedColumns: ["branch_id"];
					},
				];
			};
			tags: {
				Row: {
					tag_desc: string | null;
					tag_id: string;
					tag_name: string;
				};
				Insert: {
					tag_desc?: string | null;
					tag_id?: string;
					tag_name: string;
				};
				Update: {
					tag_desc?: string | null;
					tag_id?: string;
					tag_name?: string;
				};
				Relationships: [];
			};
			user_roles: {
				Row: {
					role_id: string;
					user_id: string;
				};
				Insert: {
					role_id?: string;
					user_id: string;
				};
				Update: {
					role_id?: string;
					user_id?: string;
				};
				Relationships: [
					{
						foreignKeyName: "user_roles_role_id_fkey";
						columns: ["role_id"];
						isOneToOne: false;
						referencedRelation: "roles";
						referencedColumns: ["role_id"];
					},
					{
						foreignKeyName: "user_roles_user_id_fkey";
						columns: ["user_id"];
						isOneToOne: false;
						referencedRelation: "users";
						referencedColumns: ["user_id"];
					},
				];
			};
			users: {
				Row: {
					branch_id: string;
					created_at: string;
					email: string;
					employee_status:
						| Database["public"]["Enums"]["employee_status"]
						| null;
					full_name: string;
					is_active: boolean | null;
					last_login_time: string | null;
					last_logout_time: string | null;
					phone: string | null;
					shift: Json;
					updated_at: string | null;
					user_id: string;
				};
				Insert: {
					branch_id: string;
					created_at?: string;
					email: string;
					employee_status?:
						| Database["public"]["Enums"]["employee_status"]
						| null;
					full_name: string;
					is_active?: boolean | null;
					last_login_time?: string | null;
					last_logout_time?: string | null;
					phone?: string | null;
					shift?: Json;
					updated_at?: string | null;
					user_id: string;
				};
				Update: {
					branch_id?: string;
					created_at?: string;
					email?: string;
					employee_status?:
						| Database["public"]["Enums"]["employee_status"]
						| null;
					full_name?: string;
					is_active?: boolean | null;
					last_login_time?: string | null;
					last_logout_time?: string | null;
					phone?: string | null;
					shift?: Json;
					updated_at?: string | null;
					user_id?: string;
				};
				Relationships: [
					{
						foreignKeyName: "users_branch_id_fkey";
						columns: ["branch_id"];
						isOneToOne: false;
						referencedRelation: "branches";
						referencedColumns: ["branch_id"];
					},
				];
			};
		};
		Views: {
			[_ in never]: never;
		};
		Functions: {
			decrease_stock:
				| { Args: { amount: number; id: string }; Returns: undefined }
				| {
					Args: {
						amount: number;
						id: string;
						notes: string;
						staff_id: string;
					};
					Returns: undefined;
				};
			generate_custom_id: { Args: { p_prefix: string }; Returns: string };
			generate_sales_report: {
				Args: {
					p_currency: string;
					p_granularity:
						Database["public"]["Enums"]["sales_report_granularity"];
					p_period_end: string;
					p_period_start: string;
				};
				Returns: undefined;
			};
		};
		Enums: {
			adjustment_type:
				| "DIRECT"
				| "INCREMENT"
				| "DECREMENT"
				| "ORDER_COMPLETION";
			DOTW: "SUN" | "MON" | "TUE" | "WED" | "THU" | "FRI" | "SAT";
			employee_status: "ACTIVE" | "INACTIVE" | "BREAK" | "OFF_DUTY";
			sales_report_granularity: "DAILY" | "WEEKLY" | "MONTHLY" | "YEARLY";
			status: "PREPARING" | "FINISHED" | "CANCELED";
		};
		CompositeTypes: {
			[_ in never]: never;
		};
	};
};

type DatabaseWithoutInternals = Omit<Database, "__InternalSupabase">;

type DefaultSchema =
	DatabaseWithoutInternals[Extract<keyof Database, "public">];

export type Tables<
	DefaultSchemaTableNameOrOptions extends
		| keyof (DefaultSchema["Tables"] & DefaultSchema["Views"])
		| { schema: keyof DatabaseWithoutInternals },
	TableName extends DefaultSchemaTableNameOrOptions extends {
		schema: keyof DatabaseWithoutInternals;
	} ? keyof (
			& DatabaseWithoutInternals[
				DefaultSchemaTableNameOrOptions["schema"]
			]["Tables"]
			& DatabaseWithoutInternals[
				DefaultSchemaTableNameOrOptions["schema"]
			]["Views"]
		)
		: never = never,
> = DefaultSchemaTableNameOrOptions extends {
	schema: keyof DatabaseWithoutInternals;
} ? (
		& DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]][
			"Tables"
		]
		& DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]][
			"Views"
		]
	)[TableName] extends {
		Row: infer R;
	} ? R
	: never
	: DefaultSchemaTableNameOrOptions extends keyof (
		& DefaultSchema["Tables"]
		& DefaultSchema["Views"]
	) ? (
			& DefaultSchema["Tables"]
			& DefaultSchema["Views"]
		)[DefaultSchemaTableNameOrOptions] extends {
			Row: infer R;
		} ? R
		: never
	: never;

export type TablesInsert<
	DefaultSchemaTableNameOrOptions extends
		| keyof DefaultSchema["Tables"]
		| { schema: keyof DatabaseWithoutInternals },
	TableName extends DefaultSchemaTableNameOrOptions extends {
		schema: keyof DatabaseWithoutInternals;
	}
		? keyof DatabaseWithoutInternals[
			DefaultSchemaTableNameOrOptions["schema"]
		]["Tables"]
		: never = never,
> = DefaultSchemaTableNameOrOptions extends {
	schema: keyof DatabaseWithoutInternals;
}
	? DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]][
		"Tables"
	][TableName] extends {
		Insert: infer I;
	} ? I
	: never
	: DefaultSchemaTableNameOrOptions extends keyof DefaultSchema["Tables"]
		? DefaultSchema["Tables"][DefaultSchemaTableNameOrOptions] extends {
			Insert: infer I;
		} ? I
		: never
	: never;

export type TablesUpdate<
	DefaultSchemaTableNameOrOptions extends
		| keyof DefaultSchema["Tables"]
		| { schema: keyof DatabaseWithoutInternals },
	TableName extends DefaultSchemaTableNameOrOptions extends {
		schema: keyof DatabaseWithoutInternals;
	}
		? keyof DatabaseWithoutInternals[
			DefaultSchemaTableNameOrOptions["schema"]
		]["Tables"]
		: never = never,
> = DefaultSchemaTableNameOrOptions extends {
	schema: keyof DatabaseWithoutInternals;
}
	? DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]][
		"Tables"
	][TableName] extends {
		Update: infer U;
	} ? U
	: never
	: DefaultSchemaTableNameOrOptions extends keyof DefaultSchema["Tables"]
		? DefaultSchema["Tables"][DefaultSchemaTableNameOrOptions] extends {
			Update: infer U;
		} ? U
		: never
	: never;

export type Enums<
	DefaultSchemaEnumNameOrOptions extends
		| keyof DefaultSchema["Enums"]
		| { schema: keyof DatabaseWithoutInternals },
	EnumName extends DefaultSchemaEnumNameOrOptions extends {
		schema: keyof DatabaseWithoutInternals;
	}
		? keyof DatabaseWithoutInternals[
			DefaultSchemaEnumNameOrOptions["schema"]
		]["Enums"]
		: never = never,
> = DefaultSchemaEnumNameOrOptions extends {
	schema: keyof DatabaseWithoutInternals;
}
	? DatabaseWithoutInternals[DefaultSchemaEnumNameOrOptions["schema"]][
		"Enums"
	][EnumName]
	: DefaultSchemaEnumNameOrOptions extends keyof DefaultSchema["Enums"]
		? DefaultSchema["Enums"][DefaultSchemaEnumNameOrOptions]
	: never;

export type CompositeTypes<
	PublicCompositeTypeNameOrOptions extends
		| keyof DefaultSchema["CompositeTypes"]
		| { schema: keyof DatabaseWithoutInternals },
	CompositeTypeName extends PublicCompositeTypeNameOrOptions extends {
		schema: keyof DatabaseWithoutInternals;
	}
		? keyof DatabaseWithoutInternals[
			PublicCompositeTypeNameOrOptions["schema"]
		]["CompositeTypes"]
		: never = never,
> = PublicCompositeTypeNameOrOptions extends {
	schema: keyof DatabaseWithoutInternals;
}
	? DatabaseWithoutInternals[PublicCompositeTypeNameOrOptions["schema"]][
		"CompositeTypes"
	][CompositeTypeName]
	: PublicCompositeTypeNameOrOptions extends
		keyof DefaultSchema["CompositeTypes"]
		? DefaultSchema["CompositeTypes"][PublicCompositeTypeNameOrOptions]
	: never;

export const Constants = {
	public: {
		Enums: {
			adjustment_type: [
				"DIRECT",
				"INCREMENT",
				"DECREMENT",
				"ORDER_COMPLETION",
			],
			DOTW: ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"],
			employee_status: ["ACTIVE", "INACTIVE", "BREAK", "OFF_DUTY"],
			sales_report_granularity: ["DAILY", "WEEKLY", "MONTHLY", "YEARLY"],
			status: ["PREPARING", "FINISHED", "CANCELED"],
		},
	},
} as const;
