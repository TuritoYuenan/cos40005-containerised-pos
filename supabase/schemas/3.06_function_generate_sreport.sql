-- Triggered by Supabase cron
-- Generate sales report
-- Accepts granularity, period_start, period_end, and currency as parameters
-- Inserts a new record into the sales_reports table with the calculated values
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
  -- Calculate gross sales, discount total, net sales, and orders count based on the provided parameters
  SELECT
	COALESCE(SUM(o.total_amount), 0),
	COALESCE(SUM(o.discount_amount), 0),
	COALESCE(SUM(o.net_amount), 0),
	COUNT(o.id)
  INTO
	v_gross_sales,
	v_discount_total,
	v_net_sales,
	v_orders_count
  FROM public.orders o
  WHERE o.order_date >= p_period_start
    AND o.order_date <= p_period_end
    AND o.currency = p_currency;

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
	created_at
  ) VALUES (
	p_granularity,
	p_period_start,
	p_period_end,
	p_currency,
	v_gross_sales,
	v_discount_total,
	v_net_sales,
	v_orders_count,
	NOW()
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
