CREATE TABLE IF NOT EXISTS "public"."promotion_conditions" (
    "condition_id" character varying NOT NULL,
    "promotion_id" character varying NOT NULL,
    "condition_type" "text" NOT NULL,
    "value" "jsonb" NOT NULL
);

ALTER TABLE "public"."promotion_conditions" OWNER TO "postgres";

ALTER TABLE ONLY "public"."promotion_conditions"
    ADD CONSTRAINT "promotion_conditions_pkey" PRIMARY KEY ("condition_id");

ALTER TABLE "public"."promotion_conditions" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Enable access for all users" ON "public"."promotion_conditions" USING (true);

GRANT ALL ON TABLE "public"."promotion_conditions" TO "anon";
GRANT ALL ON TABLE "public"."promotion_conditions" TO "authenticated";
GRANT ALL ON TABLE "public"."promotion_conditions" TO "service_role";
