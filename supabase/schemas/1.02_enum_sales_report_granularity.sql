-- Create enum type for sales report granularity
CREATE TYPE public.sales_report_granularity AS ENUM (
  'DAILY',
  'WEEKLY',
  'MONTHLY',
  'YEARLY'
);
