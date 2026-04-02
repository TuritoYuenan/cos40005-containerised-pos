CREATE TABLE IF NOT EXISTS "public"."branches" (
    "branch_id" character varying DEFAULT "public"."generate_custom_id"('BRA'::"text") NOT NULL,
    "branch_name" character varying NOT NULL,
    "address" "text" NOT NULL,
    "phone_num" character varying NOT NULL,
    "email" character varying NOT NULL,
    "is_active" boolean DEFAULT true NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL
);

ALTER TABLE "public"."branches" OWNER TO "postgres";

ALTER TABLE ONLY "public"."branches"
    ADD CONSTRAINT "branches_pkey" PRIMARY KEY ("branch_id");

ALTER TABLE "public"."branches" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Enable access for all users" ON "public"."branches" USING (true);

GRANT ALL ON TABLE "public"."branches" TO "anon";
GRANT ALL ON TABLE "public"."branches" TO "authenticated";
GRANT ALL ON TABLE "public"."branches" TO "service_role";
