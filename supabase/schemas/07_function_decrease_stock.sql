CREATE OR REPLACE FUNCTION "public"."decrease_stock"(
	"id" character varying,
	"amount" numeric,
	"staff_id" "uuid",
	"notes" character varying
) RETURNS "void" LANGUAGE "sql" AS $$
update ingredients
set current_stock = current_stock - amount
where ingredient_id = id
and current_stock >= amount;

-- Add a stock adjustment record
insert into stock_adjustments (record_id, staff_id, ingredient_id, adjustment_type, quantity_before, quantity_after, timestamp, notes)
values (
	public.generate_custom_id('ADJ'::text),
	staff_id,
	id,
	'DECREMENT',
	(select current_stock + amount from ingredients where ingredient_id = id), -- quantity_before
	(select current_stock from ingredients where ingredient_id = id), -- quantity_after
	now(),
	notes
);
$$;

ALTER FUNCTION "public"."decrease_stock"("id" character varying, "amount" numeric, "staff_id" "uuid", "notes" character varying) OWNER TO "postgres";

GRANT ALL ON FUNCTION "public"."decrease_stock"("id" character varying, "amount" numeric, "staff_id" "uuid", "notes" character varying) TO "anon";
GRANT ALL ON FUNCTION "public"."decrease_stock"("id" character varying, "amount" numeric, "staff_id" "uuid", "notes" character varying) TO "authenticated";
GRANT ALL ON FUNCTION "public"."decrease_stock"("id" character varying, "amount" numeric, "staff_id" "uuid", "notes" character varying) TO "service_role";
