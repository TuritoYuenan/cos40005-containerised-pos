CREATE TABLE IF NOT EXISTS "public"."branch_item_tags" (
    "item_id" character varying NOT NULL,
    "tag_id" character varying NOT NULL
);

ALTER TABLE "public"."branch_item_tags" OWNER TO "postgres";

ALTER TABLE ONLY "public"."branch_item_tags"
    ADD CONSTRAINT "branch_item_tags_tag_id_fkey" FOREIGN KEY ("tag_id") REFERENCES "public"."tags"("tag_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "public"."branch_item_tags" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Enable access for all users" ON "public"."branch_item_tags" USING (true);

GRANT ALL ON TABLE "public"."branch_item_tags" TO "anon";
GRANT ALL ON TABLE "public"."branch_item_tags" TO "authenticated";
GRANT ALL ON TABLE "public"."branch_item_tags" TO "service_role";
