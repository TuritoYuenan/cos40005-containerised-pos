CREATE TABLE IF NOT EXISTS "public"."tables" (
    "table_id" character varying DEFAULT "public"."generate_custom_id"('TAB'::"text") NOT NULL,
    "branch_id" character varying NOT NULL,
    "table_code" "text" NOT NULL,
    "is_active" boolean DEFAULT true NOT NULL
);

ALTER TABLE "public"."tables" OWNER TO "postgres";

ALTER TABLE ONLY "public"."tables"
    ADD CONSTRAINT "tables_pkey" PRIMARY KEY ("table_id");

ALTER TABLE ONLY "public"."tables"
    ADD CONSTRAINT "tables_table_code_key" UNIQUE ("table_code");

ALTER TABLE ONLY "public"."tables"
    ADD CONSTRAINT "tables_branch_id_fkey" FOREIGN KEY ("branch_id") REFERENCES "public"."branches"("branch_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "public"."tables" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Enable access for all users" ON "public"."tables" USING (true);

GRANT ALL ON TABLE "public"."tables" TO "anon";
GRANT ALL ON TABLE "public"."tables" TO "authenticated";
GRANT ALL ON TABLE "public"."tables" TO "service_role";
