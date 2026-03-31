CREATE TABLE IF NOT EXISTS "public"."categories" (
    "category_id" character varying DEFAULT "public"."generate_custom_id"('CAT'::"text") NOT NULL,
    "category_name" character varying NOT NULL,
    "display_order" integer DEFAULT 1 NOT NULL
);

ALTER TABLE "public"."categories" OWNER TO "postgres";

ALTER TABLE ONLY "public"."categories"
    ADD CONSTRAINT "categories_pkey" PRIMARY KEY ("category_id");

ALTER TABLE "public"."categories" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Enable read access for all users" ON "public"."categories" USING (true);

GRANT ALL ON TABLE "public"."categories" TO "anon";
GRANT ALL ON TABLE "public"."categories" TO "authenticated";
GRANT ALL ON TABLE "public"."categories" TO "service_role";
