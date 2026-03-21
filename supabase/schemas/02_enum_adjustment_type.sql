CREATE TYPE "public"."adjustment_type" AS ENUM (
    'DIRECT',
    'INCREMENT',
    'DECREMENT',
    'ORDER_COMPLETION'
);

ALTER TYPE "public"."adjustment_type" OWNER TO "postgres";
