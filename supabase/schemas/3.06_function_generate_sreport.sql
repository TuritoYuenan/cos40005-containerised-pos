-- Triggered by Supabase cron
-- Generate sales report
-- Accepts granularity, period_start, period_end, and currency as parameters
-- Uses the actual orders schema: created_at, final_amount, and tax_amount
CREATE OR REPLACE FUNCTION public.generate_sales_report(
  p_granularity public.sales_report_granularity,
  p_period_start date,
  p_period_end date,
  p_currency text
) RETURNS void LANGUAGE "plpgsql" SECURITY DEFINER AS $$
DECLARE
  v_gross_sales numeric(20, 2);
  v_discount_total numeric(20, 2);
  v_net_sales numeric(20, 2);
  v_orders_count bigint;
BEGIN
  -- Calculate totals from the actual orders table columns.
  -- The schema does not store discounts or currency on orders, so discount_total stays zero
  -- and the currency is stored only on the generated report row.
  SELECT
  	COALESCE(SUM(o.final_amount)::numeric(20, 2), 0),
  	0::numeric(20, 2),
  	COALESCE(SUM((o.final_amount - COALESCE(o.tax_amount, 0)))::numeric(20, 2), 0),
  	COUNT(o.order_id)
  INTO
	v_gross_sales,
	v_discount_total,
	v_net_sales,
	v_orders_count
  FROM public.orders o
  WHERE o.created_at::date >= p_period_start
    AND o.created_at::date <= p_period_end;

  -- Insert the calculated values into the sales_reports table
  INSERT INTO public.sales_reports (
	granularity,
	period_start,
	period_end,
	currency,
	gross_sales,
	discount_total,
	net_sales,
	orders_count,
  	source_job
  ) VALUES (
	p_granularity,
	p_period_start,
	p_period_end,
	p_currency,
	v_gross_sales,
	v_discount_total,
	v_net_sales,
	v_orders_count,
  	'generate_sales_report'
  );
END;
$$;

ALTER FUNCTION public.generate_sales_report(
  p_granularity public.sales_report_granularity,
  p_period_start date,
  p_period_end date,
  p_currency text
) OWNER TO "postgres";

GRANT ALL ON FUNCTION public.generate_sales_report(
  p_granularity public.sales_report_granularity,
  p_period_start date,
  p_period_end date,
  p_currency text
) TO "anon";
GRANT ALL ON FUNCTION public.generate_sales_report(
  p_granularity public.sales_report_granularity,
  p_period_start date,
  p_period_end date,
  p_currency text
) TO "authenticated";
GRANT ALL ON FUNCTION public.generate_sales_report(
  p_granularity public.sales_report_granularity,
  p_period_start date,
  p_period_end date,
  p_currency text
) TO "service_role";
