CREATE TABLE IF NOT EXISTS "public"."promotions" (
    "promotion_id" character varying NOT NULL,
    "branch_id" character varying NOT NULL,
    "name" "text" NOT NULL,
    "description" "text",
    "is_active" boolean DEFAULT true NOT NULL,
    "start_date" timestamp without time zone,
    "end_date" timestamp without time zone,
    "requires_manual_confirmation" boolean DEFAULT false NOT NULL,
    "notes" "text",
    "url_img" character varying
);

ALTER TABLE "public"."promotions" OWNER TO "postgres";

ALTER TABLE ONLY "public"."promotions"
    ADD CONSTRAINT "promotions_pkey" PRIMARY KEY ("promotion_id");

ALTER TABLE ONLY "public"."promotions"
    ADD CONSTRAINT "promotions_branch_id_fkey" FOREIGN KEY ("branch_id") REFERENCES "public"."branches"("branch_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "public"."promotions" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow all access to promotions" ON "public"."promotions" USING (true) WITH CHECK (true);

GRANT ALL ON TABLE "public"."promotions" TO "anon";
GRANT ALL ON TABLE "public"."promotions" TO "authenticated";
GRANT ALL ON TABLE "public"."promotions" TO "service_role";
