CREATE TABLE IF NOT EXISTS "public"."item_ingredients" (
    "ingredient_id" character varying NOT NULL,
    "item_id" character varying NOT NULL,
    "quantity" numeric,
    "unit" character varying
);

ALTER TABLE "public"."item_ingredients" OWNER TO "postgres";

ALTER TABLE ONLY "public"."item_ingredients"
    ADD CONSTRAINT "item_ingredients_pkey" PRIMARY KEY ("ingredient_id", "item_id");

ALTER TABLE ONLY "public"."item_ingredients"
    ADD CONSTRAINT "item_ingredients_ingredient_id_fkey" FOREIGN KEY ("ingredient_id") REFERENCES "public"."ingredients"("ingredient_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."item_ingredients"
    ADD CONSTRAINT "item_ingredients_item_id_fkey" FOREIGN KEY ("item_id") REFERENCES "public"."branch_items"("item_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "public"."item_ingredients" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Enable access for all users" ON "public"."item_ingredients" USING (true);

GRANT ALL ON TABLE "public"."item_ingredients" TO "anon";
GRANT ALL ON TABLE "public"."item_ingredients" TO "authenticated";
GRANT ALL ON TABLE "public"."item_ingredients" TO "service_role";
