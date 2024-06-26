package nl.lucashuls.kentekencheck.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFUtil {
    // Create a PDF file with the given content and return the file
    public static File createPDF(Context context, String kenteken, String content) throws IOException {
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build();

        PrintedPdfDocument pdfDocument = getPrintedPdfDocument(context, content, printAttributes);

        String fileName = String.format("Kentekenrapport (%s).pdf", kenteken);
        File pdfFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
        FileOutputStream fos = new FileOutputStream(pdfFile);
        pdfDocument.writeTo(fos);

        pdfDocument.close();
        fos.close();

        return pdfFile;
    }

    // Get a PrintedPdfDocument with the given content and print attributes
    private static @NonNull PrintedPdfDocument getPrintedPdfDocument(Context context, String content, PrintAttributes printAttributes) {
        PrintedPdfDocument pdfDocument = new PrintedPdfDocument(context, printAttributes);
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);

        int x = 10, y = 25;
        int maxHeight = pageInfo.getPageHeight() - 50;
        for (String line : content.split("\n")) {
            if (y > maxHeight) {
                pdfDocument.finishPage(page);
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 25;
            }
            canvas.drawText(line, x, y, paint);
            y += (int) (paint.descent() - paint.ascent());
        }

        pdfDocument.finishPage(page);
        return pdfDocument;
    }
}
