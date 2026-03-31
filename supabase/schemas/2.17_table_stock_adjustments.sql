CREATE TABLE IF NOT EXISTS "public"."stock_adjustments" (
    "record_id" character varying DEFAULT "public"."generate_custom_id"('STA'::"text") NOT NULL,
    "staff_id" "uuid",
    "ingredient_id" character varying NOT NULL,
    "adjustment_type" "public"."adjustment_type" NOT NULL,
    "quantity_before" numeric NOT NULL,
    "quantity_after" numeric NOT NULL,
    "timestamp" timestamp with time zone DEFAULT "now"() NOT NULL,
    "notes" character varying
);

ALTER TABLE "public"."stock_adjustments" OWNER TO "postgres";

ALTER TABLE ONLY "public"."stock_adjustments"
    ADD CONSTRAINT "stock_adjustments_pkey" PRIMARY KEY ("record_id");

ALTER TABLE ONLY "public"."stock_adjustments"
    ADD CONSTRAINT "stock_adjustments_ingredient_id_fkey" FOREIGN KEY ("ingredient_id") REFERENCES "public"."ingredients"("ingredient_id") ON DELETE CASCADE;

ALTER TABLE ONLY "public"."stock_adjustments"
    ADD CONSTRAINT "stock_adjustments_staff_id_fkey1" FOREIGN KEY ("staff_id") REFERENCES "public"."users"("user_id") ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE "public"."stock_adjustments" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Enable access for all users" ON "public"."stock_adjustments" USING (true);

GRANT ALL ON TABLE "public"."stock_adjustments" TO "anon";
GRANT ALL ON TABLE "public"."stock_adjustments" TO "authenticated";
GRANT ALL ON TABLE "public"."stock_adjustments" TO "service_role";
