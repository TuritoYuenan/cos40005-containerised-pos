DROP POLICY "Enable access for all users" ON "public"."branch_item_tags";

REVOKE DELETE ON TABLE "public"."branch_item_tags" FROM "anon";

REVOKE INSERT ON TABLE "public"."branch_item_tags" FROM "anon";

REVOKE REFERENCES ON TABLE "public"."branch_item_tags" FROM "anon";

REVOKE SELECT ON TABLE "public"."branch_item_tags" FROM "anon";

REVOKE TRIGGER ON TABLE "public"."branch_item_tags" FROM "anon";

REVOKE TRUNCATE ON TABLE "public"."branch_item_tags" FROM "anon";

REVOKE UPDATE ON TABLE "public"."branch_item_tags" FROM "anon";

REVOKE DELETE ON TABLE "public"."branch_item_tags" FROM "authenticated";

REVOKE INSERT ON TABLE "public"."branch_item_tags" FROM "authenticated";

REVOKE REFERENCES ON TABLE "public"."branch_item_tags" FROM "authenticated";

REVOKE SELECT ON TABLE "public"."branch_item_tags" FROM "authenticated";

REVOKE TRIGGER ON TABLE "public"."branch_item_tags" FROM "authenticated";

REVOKE TRUNCATE ON TABLE "public"."branch_item_tags" FROM "authenticated";

REVOKE UPDATE ON TABLE "public"."branch_item_tags" FROM "authenticated";

REVOKE DELETE ON TABLE "public"."branch_item_tags" FROM "service_role";

REVOKE INSERT ON TABLE "public"."branch_item_tags" FROM "service_role";

REVOKE REFERENCES ON TABLE "public"."branch_item_tags" FROM "service_role";

REVOKE SELECT ON TABLE "public"."branch_item_tags" FROM "service_role";

REVOKE TRIGGER ON TABLE "public"."branch_item_tags" FROM "service_role";

REVOKE TRUNCATE ON TABLE "public"."branch_item_tags" FROM "service_role";

REVOKE UPDATE ON TABLE "public"."branch_item_tags" FROM "service_role";

ALTER TABLE "public"."branch_item_tags"
	DROP CONSTRAINT "branch_item_tags_tag_id_fkey";

DROP TABLE "public"."branch_item_tags";

CREATE TABLE "public"."item_tags" (
	"item_id" character varying NOT NULL,
	"tag_id" character varying NOT NULL
);

ALTER TABLE "public"."item_tags" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."tags"
	ALTER COLUMN "tag_id" SET DEFAULT public.generate_custom_id ('TAG'::text);

ALTER TABLE "public"."item_tags"
	ADD CONSTRAINT "branch_item_tags_tag_id_fkey" FOREIGN KEY (tag_id) REFERENCES public.tags (tag_id) ON UPDATE CASCADE ON DELETE CASCADE NOT valid;

ALTER TABLE "public"."item_tags" validate CONSTRAINT "branch_item_tags_tag_id_fkey";

SET check_function_bodies = OFF;

CREATE OR REPLACE FUNCTION public.decrease_stock (id character varying, amount numeric, staff_id uuid, notes character varying)
	RETURNS void
	LANGUAGE sql
	AS $function$
	UPDATE
		ingredients
	SET
		current_stock = current_stock - amount
	WHERE
		ingredient_id = id
		AND current_stock >= amount;

	-- Add a stock adjustment record
	INSERT INTO stock_adjustments (
		record_id, staff_id, ingredient_id, adjustment_type,
		quantity_before, quantity_after, timestamp, notes
	) VALUES (
		public.generate_custom_id ('ADJ'::text), staff_id, id, 'DECREMENT',
		(SELECT current_stock + amount FROM ingredients WHERE ingredient_id = id), -- quantity_before
		(SELECT current_stock FROM ingredients WHERE ingredient_id = id), -- quantity_after
		now(), notes
	);
$function$;

CREATE OR REPLACE FUNCTION public.decrease_stock (id character varying, amount numeric)
	RETURNS void
	LANGUAGE sql
	AS $function$
	UPDATE
		ingredients
	SET
		current_stock = current_stock - amount
	WHERE
		ingredient_id = id
		AND current_stock >= amount;

	-- Add a stock adjustment record
	INSERT INTO stock_adjustments (
		record_id, staff_id, ingredient_id, adjustment_type,
		quantity_before, quantity_after, timestamp, notes
	) VALUES (
		public.generate_custom_id ('ADJ'::text),
		'37a7dccc-8a13-44ce-9981-efd5ed84d71c', -- Example staff
		id,
		'DECREMENT',
		(SELECT current_stock + amount FROM ingredients WHERE ingredient_id = id), -- quantity_before
		(SELECT current_stock FROM ingredients WHERE ingredient_id = id), -- quantity_after
		now(),
		'Stock decreased by ' || amount
	);
$function$;

CREATE OR REPLACE FUNCTION public.generate_custom_id (p_prefix text)
	RETURNS text
	LANGUAGE plpgsql
	SECURITY DEFINER
	AS $function$
DECLARE
	today date := CURRENT_DATE;
	next_seq integer;
	date_part text;
BEGIN
	-- Optional safety check
	IF length(p_prefix) <> 3 THEN
		RAISE EXCEPTION 'Prefix must be exactly 3 characters';
	END IF;
	INSERT INTO function.daily_sequence (prefix, seq_date, seq)
		VALUES (upper(p_prefix), today, 0)
	ON CONFLICT (prefix, seq_date)
		DO UPDATE SET
			seq = daily_sequence.seq + 1
		RETURNING
			seq INTO next_seq;
	date_part := to_char(today, 'YYMMDD');
	RETURN upper(p_prefix) || date_part || lpad(next_seq::text, 2, '0');
END;
$function$;

CREATE OR REPLACE FUNCTION public.rls_auto_enable ()
	RETURNS event_trigger
	LANGUAGE plpgsql
	SECURITY DEFINER
	SET search_path TO 'pg_catalog'
	AS $function$
DECLARE
	cmd record;
BEGIN
	FOR cmd IN
	SELECT
		*
	FROM
		pg_event_trigger_ddl_commands ()
	WHERE
		command_tag IN ('CREATE TABLE', 'CREATE TABLE AS', 'SELECT INTO')
		AND object_type IN ('table', 'partitioned table')
		LOOP
			IF cmd.schema_name IS NOT NULL AND cmd.schema_name IN ('public') AND cmd.schema_name NOT IN ('pg_catalog', 'information_schema') AND cmd.schema_name NOT LIKE 'pg_toast%' AND cmd.schema_name NOT LIKE 'pg_temp%' THEN
				BEGIN
					EXECUTE format('alter table if exists %s enable row level security', cmd.object_identity);
					RAISE LOG 'rls_auto_enable: enabled RLS on %', cmd.object_identity;
				EXCEPTION
					WHEN OTHERS THEN
						RAISE LOG 'rls_auto_enable: failed to enable RLS on %', cmd.object_identity;
				END;
			ELSE
				RAISE LOG 'rls_auto_enable: skip % (either system schema or not in enforced list: %.)', cmd.object_identity, cmd.schema_name;
			END IF;
	END LOOP;
END;

$function$;

GRANT DELETE ON TABLE "public"."item_tags" TO "anon";

GRANT INSERT ON TABLE "public"."item_tags" TO "anon";

GRANT REFERENCES ON TABLE "public"."item_tags" TO "anon";

GRANT SELECT ON TABLE "public"."item_tags" TO "anon";

GRANT TRIGGER ON TABLE "public"."item_tags" TO "anon";

GRANT TRUNCATE ON TABLE "public"."item_tags" TO "anon";

GRANT UPDATE ON TABLE "public"."item_tags" TO "anon";

GRANT DELETE ON TABLE "public"."item_tags" TO "authenticated";

GRANT INSERT ON TABLE "public"."item_tags" TO "authenticated";

GRANT REFERENCES ON TABLE "public"."item_tags" TO "authenticated";

GRANT SELECT ON TABLE "public"."item_tags" TO "authenticated";

GRANT TRIGGER ON TABLE "public"."item_tags" TO "authenticated";

GRANT TRUNCATE ON TABLE "public"."item_tags" TO "authenticated";

GRANT UPDATE ON TABLE "public"."item_tags" TO "authenticated";

GRANT DELETE ON TABLE "public"."item_tags" TO "service_role";

GRANT INSERT ON TABLE "public"."item_tags" TO "service_role";

GRANT REFERENCES ON TABLE "public"."item_tags" TO "service_role";

GRANT SELECT ON TABLE "public"."item_tags" TO "service_role";

GRANT TRIGGER ON TABLE "public"."item_tags" TO "service_role";

GRANT TRUNCATE ON TABLE "public"."item_tags" TO "service_role";

GRANT UPDATE ON TABLE "public"."item_tags" TO "service_role";

CREATE POLICY "Enable access for all users" ON "public"."item_tags" AS permissive
	FOR ALL TO public
	USING (TRUE);

CREATE POLICY "Allow all access to tags" ON "public"."tags" AS permissive
	FOR ALL TO anon
	USING (TRUE)
	WITH CHECK (TRUE);
