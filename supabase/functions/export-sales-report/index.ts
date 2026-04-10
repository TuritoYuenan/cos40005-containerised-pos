import '@supabase/functions-js/edge-runtime.d.ts';
import { createClient } from '@supabase/supabase-js';
import { StandardFonts, PDFDocument, PageSizes } from 'pdf-lib';
import { Database, Tables } from './models.ts';

/** Supabase client */
const supabase = createClient<Database>(
	Deno.env.get('SUPABASE_URL'),
	Deno.env.get('SUPABASE_ANON_KEY'),
);

/** Display the sales report period */
function formatPeriod(report: Tables<'sales_reports'>) {
	const periodStart = new Date(report.period_start);
	const periodEnd = new Date(report.period_end);

	switch (report.granularity) {
		case 'DAILY':
			return periodStart.toLocaleDateString();
		case 'WEEKLY':
			return `${periodStart.toLocaleDateString()} - ${periodEnd.toLocaleDateString()}`;
		case 'MONTHLY':
			return periodStart.toLocaleString('default', {
				month: 'long',
				year: 'numeric',
			});
		case 'YEARLY':
			return periodStart.getFullYear().toString();
		default:
			return 'Unknown Period';
	}
}

/** Build a PDF document with the sales report data */
async function buildPDF(data: Tables<'sales_reports'>) {
	const report = await PDFDocument.create();
	const page = report.addPage(PageSizes.A4);
	const { height } = page.getSize();

	page.setFont(await report.embedFont(StandardFonts.HelveticaBold));
	page.moveTo(50, height - 50);
	page.drawText(
		`${data.granularity} Report on sales statistics\n`,
		{ size: 24 },
	);

	page.moveDown(50);
	page.drawText(
		`Period: ${formatPeriod(data)}\n` +
			`During this period, there were ${data.orders_count} orders.\n` +
			`Gross Sales: ${data.gross_sales} ${data.currency}\n` +
			`Discount Total: ${data.discount_total} ${data.currency}\n` +
			`Net Sales: ${data.net_sales} ${data.currency}`,
		{ size: 16 },
	);

	return report;
}

/**
 * This function generates a sales report in PDF format,
 * uploads it to Supabase Storage, and returns a link to the report.
 *
 * A Supabase Cron job runs an SQL function to insert daily sales report data
 * into the database table "sales_reports". This data is available to the user.
 *
 * This edge function runs on user clicking the "Download Report" button.
 * The function receives the "report_id" as a payload, which is used to fetch the relevant sales data from the database.
 *
 * @param req - Receive payload with key "report_id"
 */
Deno.serve(async (req) => {
	const { report_id } = await req.json();

	const { data: reportData, error } = await supabase
		.from('sales_reports')
		.select('*')
		.eq('id', report_id)
		.single();

	if (error) {
		return new Response(
			JSON.stringify({ error: 'Report not found' }),
			{ headers: { 'Content-Type': 'application/json' }, status: 404 },
		);
	}

	const pdfRaw = await buildPDF(reportData).then((pdf) => pdf.save());

	const { data: upload, error: uploadError } = await supabase.storage
		.from('sales-reports')
		.upload(`report-${reportData.id}.pdf`, pdfRaw, {
			contentType: 'application/pdf',
			upsert: true,
		});

	if (uploadError) {
		return new Response(
			JSON.stringify({ error: uploadError.message }),
			{ headers: { 'Content-Type': 'application/json' }, status: 500 },
		);
	}

	const { data: publicUrlData } = supabase.storage
		.from('sales-reports')
		.getPublicUrl(upload.path);

	return new Response(
		JSON.stringify({ url: publicUrlData.publicUrl }),
		{ headers: { 'Content-Type': 'application/json' } },
	);
});
