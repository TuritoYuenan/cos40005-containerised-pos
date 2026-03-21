CREATE TYPE "public"."status" AS ENUM (
    'PREPARING',
    'FINISHED',
    'CANCELED'
);

ALTER TYPE "public"."status" OWNER TO "postgres";
