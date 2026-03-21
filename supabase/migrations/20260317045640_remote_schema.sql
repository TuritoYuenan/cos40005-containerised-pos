SET statement_timeout = 0;

SET lock_timeout = 0;

SET idle_in_transaction_session_timeout = 0;

SET client_encoding = 'UTF8';

SET standard_conforming_strings = ON;

SELECT
    pg_catalog.set_config('search_path', '', FALSE);

SET check_function_bodies = FALSE;

SET xmloption = content;

SET client_min_messages = warning;

SET row_security = OFF;

CREATE SCHEMA IF NOT EXISTS "function";

ALTER SCHEMA "function" OWNER TO "postgres";

COMMENT ON SCHEMA "public" IS 'standard public schema';

CREATE EXTENSION IF NOT EXISTS "pg_graphql" WITH SCHEMA "graphql";

CREATE EXTENSION IF NOT EXISTS "pg_stat_statements" WITH SCHEMA "extensions";

CREATE EXTENSION IF NOT EXISTS "pgcrypto" WITH SCHEMA "extensions";

CREATE EXTENSION IF NOT EXISTS "supabase_vault" WITH SCHEMA "vault";

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA "extensions";

CREATE TYPE "public"."adjustment_type" AS ENUM (
    'DIRECT',
    'INCREMENT',
    'DECREMENT',
    'ORDER_COMPLETION'
);

ALTER TYPE "public"."adjustment_type" OWNER TO "postgres";

CREATE TYPE "public"."status" AS ENUM (
    'PREPARING',
    'FINISHED',
    'CANCELED'
);

ALTER TYPE "public"."status" OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."create_user_profile" ()
    RETURNS "trigger"
    LANGUAGE "plpgsql"
    SECURITY DEFINER
    AS $$
BEGIN
  INSERT INTO public.users (user_id, email, phone)
  VALUES (NEW.id, NEW.email, NEW.phone)
  ON CONFLICT (user_id) DO NOTHING;
  RETURN NEW;
END;
$$;

ALTER FUNCTION "public"."create_user_profile" () OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."decrease_stock" ("id" character varying, "amount" numeric)
    RETURNS "void"
    LANGUAGE "sql"
    AS $$
update ingredients
set current_stock = current_stock - amount
where ingredient_id = id
and current_stock >= amount;
$$;

ALTER FUNCTION "public"."decrease_stock" ("id" character varying, "amount" numeric) OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."generate_custom_id" ("p_prefix" "text")
    RETURNS "text"
    LANGUAGE "plpgsql"
    AS $$
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
$$;

ALTER FUNCTION "public"."generate_custom_id" ("p_prefix" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."rls_auto_enable" ()
    RETURNS "event_trigger"
    LANGUAGE "plpgsql"
    SECURITY DEFINER
    SET "search_path" TO 'pg_catalog'
    AS $$
DECLARE
  cmd record;
BEGIN
  FOR cmd IN
    SELECT *
    FROM pg_event_trigger_ddl_commands()
    WHERE command_tag IN ('CREATE TABLE', 'CREATE TABLE AS', 'SELECT INTO')
      AND object_type IN ('table','partitioned table')
  LOOP
     IF cmd.schema_name IS NOT NULL AND cmd.schema_name IN ('public') AND cmd.schema_name NOT IN ('pg_catalog','information_schema') AND cmd.schema_name NOT LIKE 'pg_toast%' AND cmd.schema_name NOT LIKE 'pg_temp%' THEN
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
$$;

ALTER FUNCTION "public"."rls_auto_enable" () OWNER TO "postgres";

SET default_tablespace = '';

SET default_table_access_method = "heap";

CREATE TABLE IF NOT EXISTS "function"."daily_sequence" (
    "prefix" "text" NOT NULL,
    "seq_date" "date" NOT NULL,
    "seq" integer NOT NULL
);

ALTER TABLE "function"."daily_sequence" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."branch_item_tags" (
    "item_id" character varying NOT NULL,
    "tag_id" character varying NOT NULL
);

ALTER TABLE "public"."branch_item_tags" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."branch_items" (
    "item_id" character varying DEFAULT "public"."generate_custom_id" ('ITM'::"text") NOT NULL,
    "branch_id" character varying NOT NULL,
    "category_id" character varying,
    "item_name" character varying NOT NULL,
    "item_desc" "text",
    "price" integer NOT NULL,
    "estimated_prep" character varying NOT NULL,
    "is_available" boolean DEFAULT FALSE NOT NULL,
    "is_featured" boolean DEFAULT FALSE NOT NULL,
    "url_img" character varying
);

ALTER TABLE "public"."branch_items" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."branches" (
    "branch_id" character varying DEFAULT "public"."generate_custom_id" ('BRA'::"text") NOT NULL,
    "branch_name" character varying NOT NULL,
    "address" "text" NOT NULL,
    "phone_num" character varying NOT NULL,
    "email" character varying NOT NULL,
    "is_active" boolean DEFAULT TRUE NOT NULL,
    "created_at" timestamp WITH time zone DEFAULT "now" () NOT NULL
);

ALTER TABLE "public"."branches" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."categories" (
    "category_id" character varying DEFAULT "public"."generate_custom_id" ('CAT'::"text") NOT NULL,
    "category_name" character varying NOT NULL,
    "display_order" integer DEFAULT 1 NOT NULL
);

ALTER TABLE "public"."categories" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."ingredients" (
    "ingredient_id" character varying DEFAULT "public"."generate_custom_id" ('ING'::"text") NOT NULL,
    "ingredient_name" character varying,
    "unit" character varying,
    "current_stock" numeric,
    "min_stock_level" numeric,
    "supplier_info" "jsonb",
    "branch_id" character varying,
    "is_active" boolean
);

ALTER TABLE ONLY "public"."ingredients" REPLICA IDENTITY
    FULL;

ALTER TABLE "public"."ingredients" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."item_ingredients" (
    "ingredient_id" character varying NOT NULL,
    "item_id" character varying NOT NULL,
    "quantity" numeric,
    "unit" character varying
);

ALTER TABLE "public"."item_ingredients" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."order_items" (
    "order_id" character varying NOT NULL,
    "item_id" character varying NOT NULL,
    "quantity" smallint NOT NULL,
    "subtotal" integer,
    "special_notes" "text",
    "item_status" "public"."status" DEFAULT 'PREPARING'::"public"."status" NOT NULL
);

ALTER TABLE "public"."order_items" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."orders" (
    "order_id" character varying DEFAULT "public"."generate_custom_id" ('ORD'::"text") NOT NULL,
    "order_number" character varying,
    "order_type" character varying,
    "table_id" character varying,
    "status" "public"."status" DEFAULT 'PREPARING'::"public"."status" NOT NULL,
    "branch_id" character varying,
    "tax_amount" numeric,
    "final_amount" integer,
    "updated_at" timestamp WITHOUT time zone,
    "created_at" timestamp WITH time zone DEFAULT "now" () NOT NULL
);

ALTER TABLE ONLY "public"."orders" REPLICA IDENTITY
    FULL;

ALTER TABLE "public"."orders" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."promotion_conditions" (
    "condition_id" character varying NOT NULL,
    "promotion_id" character varying NOT NULL,
    "condition_type" "text" NOT NULL,
    "value" "jsonb" NOT NULL
);

ALTER TABLE "public"."promotion_conditions" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."promotions" (
    "promotion_id" character varying NOT NULL,
    "branch_id" character varying NOT NULL,
    "name" "text" NOT NULL,
    "description" "text",
    "is_active" boolean DEFAULT TRUE NOT NULL,
    "start_date" timestamp WITHOUT time zone,
    "end_date" timestamp WITHOUT time zone,
    "requires_manual_confirmation" boolean DEFAULT FALSE NOT NULL,
    "notes" "text",
    "url_img" character varying
);

ALTER TABLE "public"."promotions" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."roles" (
    "role_id" character varying DEFAULT "public"."generate_custom_id" ('ROL'::"text") NOT NULL,
    "role_name" character varying NOT NULL,
    "description" "text",
    "permission" "jsonb"
);

ALTER TABLE "public"."roles" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."stock_adjustments" (
    "record_id" character varying DEFAULT "public"."generate_custom_id" ('STA'::"text") NOT NULL,
    "staff_id" "uuid",
    "ingredient_id" character varying NOT NULL,
    "adjustment_type" "public"."adjustment_type" NOT NULL,
    "quantity_before" numeric NOT NULL,
    "quantity_after" numeric NOT NULL,
    "timestamp" timestamp WITH time zone DEFAULT "now" () NOT NULL,
    "notes" character varying
);

ALTER TABLE "public"."stock_adjustments" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."tables" (
    "table_id" character varying DEFAULT "public"."generate_custom_id" ('TAB'::"text") NOT NULL,
    "branch_id" character varying NOT NULL,
    "table_code" "text" NOT NULL,
    "is_active" boolean DEFAULT TRUE NOT NULL
);

ALTER TABLE "public"."tables" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."tags" (
    "tag_id" character varying NOT NULL,
    "tag_name" character varying NOT NULL,
    "tag_desc" "text"
);

ALTER TABLE "public"."tags" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."user_roles" (
    "user_id" "uuid" NOT NULL,
    "role_id" character varying DEFAULT ''::character varying NOT NULL
);

ALTER TABLE "public"."user_roles" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."users" (
    "user_id" "uuid" NOT NULL,
    "email" character varying NOT NULL,
    "full_name" character varying NOT NULL,
    "phone" character varying,
    "branch_id" character varying NOT NULL,
    "is_active" boolean,
    "created_at" timestamp WITH time zone DEFAULT "now" () NOT NULL,
    "updated_at" timestamp WITH time zone DEFAULT "now" ()
);

ALTER TABLE "public"."users" OWNER TO "postgres";

ALTER TABLE ONLY "function"."daily_sequence"
    ADD CONSTRAINT "daily_sequence_pkey" PRIMARY KEY ("prefix", "seq_date");

ALTER TABLE ONLY "public"."branch_items"
    ADD CONSTRAINT "branch_items_pkey" PRIMARY KEY ("item_id");

ALTER TABLE ONLY "public"."branches"
    ADD CONSTRAINT "branches_pkey" PRIMARY KEY ("branch_id");

ALTER TABLE ONLY "public"."categories"
    ADD CONSTRAINT "categories_pkey" PRIMARY KEY ("category_id");

ALTER TABLE ONLY "public"."ingredients"
    ADD CONSTRAINT "ingredients_pkey" PRIMARY KEY ("ingredient_id");

ALTER TABLE ONLY "public"."item_ingredients"
    ADD CONSTRAINT "item_ingredients_pkey" PRIMARY KEY ("ingredient_id", "item_id");

ALTER TABLE ONLY "public"."order_items"
    ADD CONSTRAINT "order_items_pkey" PRIMARY KEY ("order_id", "item_id");

ALTER TABLE ONLY "public"."orders"
    ADD CONSTRAINT "orders_pkey" PRIMARY KEY ("order_id");

ALTER TABLE ONLY "public"."promotion_conditions"
    ADD CONSTRAINT "promotion_conditions_pkey" PRIMARY KEY ("condition_id");

ALTER TABLE ONLY "public"."promotions"
    ADD CONSTRAINT "promotions_pkey" PRIMARY KEY ("promotion_id");

ALTER TABLE ONLY "public"."roles"
    ADD CONSTRAINT "roles_pkey" PRIMARY KEY ("role_id");

ALTER TABLE ONLY "public"."stock_adjustments"
    ADD CONSTRAINT "stock_adjustments_pkey" PRIMARY KEY ("record_id");

ALTER TABLE ONLY "public"."tables"
    ADD CONSTRAINT "tables_pkey" PRIMARY KEY ("table_id");

ALTER TABLE ONLY "public"."tables"
    ADD CONSTRAINT "tables_table_code_key" UNIQUE ("table_code");

ALTER TABLE ONLY "public"."tags"
    ADD CONSTRAINT "tags_pkey" PRIMARY KEY ("tag_id");

ALTER TABLE ONLY "public"."user_roles"
    ADD CONSTRAINT "user_roles_pkey" PRIMARY KEY ("user_id", "role_id");

ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_pkey" PRIMARY KEY ("user_id");

ALTER TABLE ONLY "public"."branch_item_tags"
    ADD CONSTRAINT "branch_item_tags_tag_id_fkey" FOREIGN KEY ("tag_id") REFERENCES "public"."tags" ("tag_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."branch_items"
    ADD CONSTRAINT "branch_items_branch_id_fkey" FOREIGN KEY ("branch_id") REFERENCES "public"."branches" ("branch_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."branch_items"
    ADD CONSTRAINT "branch_items_category_id_fkey" FOREIGN KEY ("category_id") REFERENCES "public"."categories" ("category_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."ingredients"
    ADD CONSTRAINT "ingredients_branch_id_fkey" FOREIGN KEY ("branch_id") REFERENCES "public"."branches" ("branch_id");

ALTER TABLE ONLY "public"."item_ingredients"
    ADD CONSTRAINT "item_ingredients_ingredient_id_fkey" FOREIGN KEY ("ingredient_id") REFERENCES "public"."ingredients" ("ingredient_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."item_ingredients"
    ADD CONSTRAINT "item_ingredients_item_id_fkey" FOREIGN KEY ("item_id") REFERENCES "public"."branch_items" ("item_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."order_items"
    ADD CONSTRAINT "order_items_item_id_fkey" FOREIGN KEY ("item_id") REFERENCES "public"."branch_items" ("item_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."order_items"
    ADD CONSTRAINT "order_items_order_id_fkey" FOREIGN KEY ("order_id") REFERENCES "public"."orders" ("order_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."orders"
    ADD CONSTRAINT "orders_branch_id_fkey" FOREIGN KEY ("branch_id") REFERENCES "public"."branches" ("branch_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."orders"
    ADD CONSTRAINT "orders_table_id_fkey" FOREIGN KEY ("table_id") REFERENCES "public"."tables" ("table_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."promotions"
    ADD CONSTRAINT "promotions_branch_id_fkey" FOREIGN KEY ("branch_id") REFERENCES "public"."branches" ("branch_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."stock_adjustments"
    ADD CONSTRAINT "stock_adjustments_ingredient_id_fkey" FOREIGN KEY ("ingredient_id") REFERENCES "public"."ingredients" ("ingredient_id") ON DELETE CASCADE;

ALTER TABLE ONLY "public"."stock_adjustments"
    ADD CONSTRAINT "stock_adjustments_staff_id_fkey1" FOREIGN KEY ("staff_id") REFERENCES "public"."users" ("user_id") ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE ONLY "public"."tables"
    ADD CONSTRAINT "tables_branch_id_fkey" FOREIGN KEY ("branch_id") REFERENCES "public"."branches" ("branch_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."user_roles"
    ADD CONSTRAINT "user_roles_role_id_fkey" FOREIGN KEY ("role_id") REFERENCES "public"."roles" ("role_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."user_roles"
    ADD CONSTRAINT "user_roles_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."users" ("user_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_branch_id_fkey" FOREIGN KEY ("branch_id") REFERENCES "public"."branches" ("branch_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "auth"."users" ("id") ON UPDATE CASCADE ON DELETE CASCADE;

CREATE POLICY "Allow all access to promotions" ON "public"."promotions"
    USING (TRUE)
    WITH CHECK (TRUE);

CREATE POLICY "Enable  access for all users" ON "public"."users"
    USING (TRUE);

CREATE POLICY "Enable access for all users" ON "public"."branch_item_tags"
    USING (TRUE);

CREATE POLICY "Enable access for all users" ON "public"."branch_items"
    USING (TRUE);

CREATE POLICY "Enable access for all users" ON "public"."branches"
    USING (TRUE);

CREATE POLICY "Enable access for all users" ON "public"."ingredients"
    USING (TRUE);

CREATE POLICY "Enable access for all users" ON "public"."item_ingredients"
    USING (TRUE);

CREATE POLICY "Enable access for all users" ON "public"."order_items"
    USING (TRUE);

CREATE POLICY "Enable access for all users" ON "public"."orders"
    USING (TRUE);

CREATE POLICY "Enable access for all users" ON "public"."promotion_conditions"
    USING (TRUE);

CREATE POLICY "Enable access for all users" ON "public"."roles"
    USING (TRUE);

CREATE POLICY "Enable access for all users" ON "public"."stock_adjustments"
    USING (TRUE);

CREATE POLICY "Enable access for all users" ON "public"."tables"
    USING (TRUE);

CREATE POLICY "Enable access for all users" ON "public"."user_roles"
    USING (TRUE);

CREATE POLICY "Enable read access for all users" ON "public"."categories"
    USING (TRUE);

CREATE POLICY "Enable read access for all users" ON "public"."tags"
    FOR SELECT
    USING (TRUE);

ALTER TABLE "public"."branch_item_tags" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."branch_items" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."branches" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."categories" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."ingredients" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."item_ingredients" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."order_items" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."orders" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."promotion_conditions" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."promotions" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."roles" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."stock_adjustments" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."tables" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."tags" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."user_roles" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."users" ENABLE ROW LEVEL SECURITY;

ALTER PUBLICATION "supabase_realtime" OWNER TO "postgres";

ALTER PUBLICATION "supabase_realtime"
    ADD TABLE ONLY "public"."ingredients";

ALTER PUBLICATION "supabase_realtime"
    ADD TABLE ONLY "public"."orders";

GRANT USAGE ON SCHEMA "public" TO "postgres";

GRANT USAGE ON SCHEMA "public" TO "anon";

GRANT USAGE ON SCHEMA "public" TO "authenticated";

GRANT USAGE ON SCHEMA "public" TO "service_role";

GRANT ALL ON FUNCTION "public"."create_user_profile" () TO "anon";

GRANT ALL ON FUNCTION "public"."create_user_profile" () TO "authenticated";

GRANT ALL ON FUNCTION "public"."create_user_profile" () TO "service_role";

GRANT ALL ON FUNCTION "public"."decrease_stock" ("id" character varying, "amount" numeric) TO "anon";

GRANT ALL ON FUNCTION "public"."decrease_stock" ("id" character varying, "amount" numeric) TO "authenticated";

GRANT ALL ON FUNCTION "public"."decrease_stock" ("id" character varying, "amount" numeric) TO "service_role";

GRANT ALL ON FUNCTION "public"."generate_custom_id" ("p_prefix" "text") TO "anon";

GRANT ALL ON FUNCTION "public"."generate_custom_id" ("p_prefix" "text") TO "authenticated";

GRANT ALL ON FUNCTION "public"."generate_custom_id" ("p_prefix" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."rls_auto_enable" () TO "anon";

GRANT ALL ON FUNCTION "public"."rls_auto_enable" () TO "authenticated";

GRANT ALL ON FUNCTION "public"."rls_auto_enable" () TO "service_role";

GRANT ALL ON TABLE "public"."branch_item_tags" TO "anon";

GRANT ALL ON TABLE "public"."branch_item_tags" TO "authenticated";

GRANT ALL ON TABLE "public"."branch_item_tags" TO "service_role";

GRANT ALL ON TABLE "public"."branch_items" TO "anon";

GRANT ALL ON TABLE "public"."branch_items" TO "authenticated";

GRANT ALL ON TABLE "public"."branch_items" TO "service_role";

GRANT ALL ON TABLE "public"."branches" TO "anon";

GRANT ALL ON TABLE "public"."branches" TO "authenticated";

GRANT ALL ON TABLE "public"."branches" TO "service_role";

GRANT ALL ON TABLE "public"."categories" TO "anon";

GRANT ALL ON TABLE "public"."categories" TO "authenticated";

GRANT ALL ON TABLE "public"."categories" TO "service_role";

GRANT ALL ON TABLE "public"."ingredients" TO "anon";

GRANT ALL ON TABLE "public"."ingredients" TO "authenticated";

GRANT ALL ON TABLE "public"."ingredients" TO "service_role";

GRANT ALL ON TABLE "public"."item_ingredients" TO "anon";

GRANT ALL ON TABLE "public"."item_ingredients" TO "authenticated";

GRANT ALL ON TABLE "public"."item_ingredients" TO "service_role";

GRANT ALL ON TABLE "public"."order_items" TO "anon";

GRANT ALL ON TABLE "public"."order_items" TO "authenticated";

GRANT ALL ON TABLE "public"."order_items" TO "service_role";

GRANT ALL ON TABLE "public"."orders" TO "anon";

GRANT ALL ON TABLE "public"."orders" TO "authenticated";

GRANT ALL ON TABLE "public"."orders" TO "service_role";

GRANT ALL ON TABLE "public"."promotion_conditions" TO "anon";

GRANT ALL ON TABLE "public"."promotion_conditions" TO "authenticated";

GRANT ALL ON TABLE "public"."promotion_conditions" TO "service_role";

GRANT ALL ON TABLE "public"."promotions" TO "anon";

GRANT ALL ON TABLE "public"."promotions" TO "authenticated";

GRANT ALL ON TABLE "public"."promotions" TO "service_role";

GRANT ALL ON TABLE "public"."roles" TO "anon";

GRANT ALL ON TABLE "public"."roles" TO "authenticated";

GRANT ALL ON TABLE "public"."roles" TO "service_role";

GRANT ALL ON TABLE "public"."stock_adjustments" TO "anon";

GRANT ALL ON TABLE "public"."stock_adjustments" TO "authenticated";

GRANT ALL ON TABLE "public"."stock_adjustments" TO "service_role";

GRANT ALL ON TABLE "public"."tables" TO "anon";

GRANT ALL ON TABLE "public"."tables" TO "authenticated";

GRANT ALL ON TABLE "public"."tables" TO "service_role";

GRANT ALL ON TABLE "public"."tags" TO "anon";

GRANT ALL ON TABLE "public"."tags" TO "authenticated";

GRANT ALL ON TABLE "public"."tags" TO "service_role";

GRANT ALL ON TABLE "public"."user_roles" TO "anon";

GRANT ALL ON TABLE "public"."user_roles" TO "authenticated";

GRANT ALL ON TABLE "public"."user_roles" TO "service_role";

GRANT ALL ON TABLE "public"."users" TO "anon";

GRANT ALL ON TABLE "public"."users" TO "authenticated";

GRANT ALL ON TABLE "public"."users" TO "service_role";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "postgres";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "anon";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "authenticated";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "service_role";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "postgres";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "anon";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "authenticated";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "service_role";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "postgres";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "anon";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "authenticated";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "service_role";

DROP EXTENSION IF EXISTS "pg_net";

CREATE POLICY "Enable insert for users " ON "storage"."buckets" AS permissive
    FOR INSERT TO public
    WITH CHECK (TRUE);

CREATE POLICY "Enable read access for all users" ON "storage"."buckets" AS permissive
    FOR SELECT TO public
    USING (TRUE);

CREATE POLICY "Give anon users access to JPG images in folder 1ffg0oo_0" ON "storage"."objects" AS permissive
    FOR SELECT TO public
    USING (((bucket_id = 'images'::text) AND (auth.role () = 'anon'::text)));

CREATE POLICY "Give anon users access to JPG images in folder 1ffg0oo_1" ON "storage"."objects" AS permissive
    FOR INSERT TO public
    WITH CHECK (((bucket_id = 'images'::text) AND (auth.role () = 'anon'::text)));

CREATE POLICY "Give anon users access to JPG images in folder 1ffg0oo_2" ON "storage"."objects" AS permissive
    FOR UPDATE TO public
    USING (((bucket_id = 'images'::text) AND (auth.role () = 'anon'::text)));
