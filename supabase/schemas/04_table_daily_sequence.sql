CREATE TABLE IF NOT EXISTS "function"."daily_sequence" (
    "prefix" "text" NOT NULL,
    "seq_date" "date" NOT NULL,
    "seq" integer NOT NULL
);

ALTER TABLE "function"."daily_sequence" OWNER TO "postgres";

ALTER TABLE ONLY "function"."daily_sequence"
    ADD CONSTRAINT "daily_sequence_pkey" PRIMARY KEY ("prefix", "seq_date");
