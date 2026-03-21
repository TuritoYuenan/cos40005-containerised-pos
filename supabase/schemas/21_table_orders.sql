CREATE TABLE IF NOT EXISTS "public"."orders" (
    "order_id" character varying DEFAULT "public"."generate_custom_id"('ORD'::"text") NOT NULL,
    "order_number" character varying,
    "order_type" character varying,
    "table_id" character varying,
    "status" "public"."status" DEFAULT 'PREPARING'::"public"."status" NOT NULL,
    "branch_id" character varying,
    "tax_amount" numeric,
    "final_amount" integer,
    "updated_at" timestamp without time zone,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL
);

ALTER TABLE ONLY "public"."orders" REPLICA IDENTITY FULL;

ALTER TABLE "public"."orders" OWNER TO "postgres";

ALTER TABLE ONLY "public"."orders"
    ADD CONSTRAINT "orders_pkey" PRIMARY KEY ("order_id");

ALTER TABLE ONLY "public"."orders"
    ADD CONSTRAINT "orders_branch_id_fkey" FOREIGN KEY ("branch_id") REFERENCES "public"."branches"("branch_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."orders"
    ADD CONSTRAINT "orders_table_id_fkey" FOREIGN KEY ("table_id") REFERENCES "public"."tables"("table_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "public"."orders" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Enable access for all users" ON "public"."orders" USING (true);

GRANT ALL ON TABLE "public"."orders" TO "anon";
GRANT ALL ON TABLE "public"."orders" TO "authenticated";
GRANT ALL ON TABLE "public"."orders" TO "service_role";

ALTER PUBLICATION "supabase_realtime" ADD TABLE ONLY "public"."orders";
