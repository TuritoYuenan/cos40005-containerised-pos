CREATE TABLE IF NOT EXISTS "public"."branch_items" (
    "item_id" character varying DEFAULT "public"."generate_custom_id"('ITM'::"text") NOT NULL,
    "branch_id" character varying NOT NULL,
    "category_id" character varying,
    "item_name" character varying NOT NULL,
    "item_desc" "text",
    "price" integer NOT NULL,
    "estimated_prep" character varying NOT NULL,
    "is_available" boolean DEFAULT false NOT NULL,
    "is_featured" boolean DEFAULT false NOT NULL,
    "url_img" character varying
);

ALTER TABLE "public"."branch_items" OWNER TO "postgres";

ALTER TABLE ONLY "public"."branch_items"
    ADD CONSTRAINT "branch_items_pkey" PRIMARY KEY ("item_id");

ALTER TABLE ONLY "public"."branch_items"
    ADD CONSTRAINT "branch_items_branch_id_fkey" FOREIGN KEY ("branch_id") REFERENCES "public"."branches"("branch_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."branch_items"
    ADD CONSTRAINT "branch_items_category_id_fkey" FOREIGN KEY ("category_id") REFERENCES "public"."categories"("category_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "public"."branch_items" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Enable access for all users" ON "public"."branch_items" USING (true);

GRANT ALL ON TABLE "public"."branch_items" TO "anon";
GRANT ALL ON TABLE "public"."branch_items" TO "authenticated";
GRANT ALL ON TABLE "public"."branch_items" TO "service_role";
