CREATE TABLE IF NOT EXISTS "public"."ingredients" (
    "ingredient_id" character varying DEFAULT "public"."generate_custom_id"('ING'::"text") NOT NULL,
    "ingredient_name" character varying,
    "unit" character varying,
    "current_stock" numeric,
    "min_stock_level" numeric,
    "supplier_info" "jsonb",
    "branch_id" character varying,
    "is_active" boolean
);

ALTER TABLE ONLY "public"."ingredients" REPLICA IDENTITY FULL;

ALTER TABLE "public"."ingredients" OWNER TO "postgres";

ALTER TABLE ONLY "public"."ingredients"
    ADD CONSTRAINT "ingredients_pkey" PRIMARY KEY ("ingredient_id");

ALTER TABLE ONLY "public"."ingredients"
    ADD CONSTRAINT "ingredients_branch_id_fkey" FOREIGN KEY ("branch_id") REFERENCES "public"."branches"("branch_id");

ALTER TABLE "public"."ingredients" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Enable access for all users" ON "public"."ingredients" USING (true);

GRANT ALL ON TABLE "public"."ingredients" TO "anon";
GRANT ALL ON TABLE "public"."ingredients" TO "authenticated";
GRANT ALL ON TABLE "public"."ingredients" TO "service_role";

ALTER PUBLICATION "supabase_realtime" ADD TABLE ONLY "public"."ingredients";
