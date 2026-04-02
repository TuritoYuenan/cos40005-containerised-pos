CREATE TABLE IF NOT EXISTS "public"."roles" (
    "role_id" character varying DEFAULT "public"."generate_custom_id"('ROL'::"text") NOT NULL,
    "role_name" character varying NOT NULL,
    "description" "text",
    "permission" "jsonb"
);

ALTER TABLE "public"."roles" OWNER TO "postgres";

ALTER TABLE ONLY "public"."roles"
    ADD CONSTRAINT "roles_pkey" PRIMARY KEY ("role_id");

ALTER TABLE "public"."roles" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Enable access for all users" ON "public"."roles" USING (true);

GRANT ALL ON TABLE "public"."roles" TO "anon";
GRANT ALL ON TABLE "public"."roles" TO "authenticated";
GRANT ALL ON TABLE "public"."roles" TO "service_role";
