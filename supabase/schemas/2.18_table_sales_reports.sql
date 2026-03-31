-- Create sales reports table
create table public.sales_reports (
  id character varying not null default generate_custom_id ('SRP'::text),
  granularity public.sales_report_granularity not null,
  period_start date not null,
  period_end date not null,
  currency text not null default 'VND'::text,
  gross_sales numeric(20, 2) not null default 0,
  discount_total numeric(20, 2) not null default 0,
  net_sales numeric(20, 2) not null default 0,
  orders_count bigint not null default 0,
  source_job text null,
  generated_at timestamp with time zone not null default now(),
  constraint sales_reports_pkey primary key (id),
  constraint sales_reports_granularity_period_start_period_end_currency_key unique (granularity, period_start, period_end, currency)
) TABLESPACE pg_default;

-- Create indexes
create index IF not exists sales_reports_period_start_idx on public.sales_reports using btree (period_start) TABLESPACE pg_default;

create index IF not exists sales_reports_granularity_period_idx on public.sales_reports using btree (granularity, period_start) TABLESPACE pg_default;
