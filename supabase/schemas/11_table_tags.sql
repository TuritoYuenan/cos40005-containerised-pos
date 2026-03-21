CREATE TABLE IF NOT EXISTS "public"."tags" (
    "tag_id" character varying NOT NULL,
    "tag_name" character varying NOT NULL,
    "tag_desc" "text"
);

ALTER TABLE "public"."tags" OWNER TO "postgres";

ALTER TABLE ONLY "public"."tags"
    ADD CONSTRAINT "tags_pkey" PRIMARY KEY ("tag_id");

ALTER TABLE "public"."tags" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Enable read access for all users" ON "public"."tags" FOR SELECT USING (true);

GRANT ALL ON TABLE "public"."tags" TO "anon";
GRANT ALL ON TABLE "public"."tags" TO "authenticated";
GRANT ALL ON TABLE "public"."tags" TO "service_role";
