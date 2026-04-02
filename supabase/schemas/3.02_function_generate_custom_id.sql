CREATE OR REPLACE FUNCTION "public"."generate_custom_id"(
	"p_prefix" "text"
) RETURNS "text" LANGUAGE "plpgsql" SECURITY DEFINER AS $$
declare
    today date := current_date;
    next_seq integer;
    date_part text;
begin
    -- Optional safety check
    if length(p_prefix) <> 3 then
        raise exception 'Prefix must be exactly 3 characters';
    end if;

    insert into function.daily_sequence (prefix, seq_date, seq)
    values (upper(p_prefix), today, 0)
    on conflict (prefix, seq_date)
    do update set seq = daily_sequence.seq + 1
    returning seq into next_seq;

    date_part := to_char(today, 'YYMMDD');

    return upper(p_prefix)
        || date_part
        || lpad(next_seq::text, 2, '0');
end;
$$;

ALTER FUNCTION "public"."generate_custom_id"("p_prefix" "text") OWNER TO "postgres";

GRANT ALL ON FUNCTION "public"."generate_custom_id"("p_prefix" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."generate_custom_id"("p_prefix" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."generate_custom_id"("p_prefix" "text") TO "service_role";
