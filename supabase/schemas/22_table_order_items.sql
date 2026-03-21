CREATE TABLE IF NOT EXISTS "public"."order_items" (
    "order_id" character varying NOT NULL,
    "item_id" character varying NOT NULL,
    "quantity" smallint NOT NULL,
    "subtotal" integer,
    "special_notes" "text",
    "item_status" "public"."status" DEFAULT 'PREPARING'::"public"."status" NOT NULL
);

ALTER TABLE "public"."order_items" OWNER TO "postgres";

ALTER TABLE ONLY "public"."order_items"
    ADD CONSTRAINT "order_items_pkey" PRIMARY KEY ("order_id", "item_id");

ALTER TABLE ONLY "public"."order_items"
    ADD CONSTRAINT "order_items_item_id_fkey" FOREIGN KEY ("item_id") REFERENCES "public"."branch_items"("item_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."order_items"
    ADD CONSTRAINT "order_items_order_id_fkey" FOREIGN KEY ("order_id") REFERENCES "public"."orders"("order_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "public"."order_items" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Enable access for all users" ON "public"."order_items" USING (true);

GRANT ALL ON TABLE "public"."order_items" TO "anon";
GRANT ALL ON TABLE "public"."order_items" TO "authenticated";
GRANT ALL ON TABLE "public"."order_items" TO "service_role";
