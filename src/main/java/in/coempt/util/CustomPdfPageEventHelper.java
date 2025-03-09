package in.coempt.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class CustomPdfPageEventHelper extends PdfPageEventHelper {
    private final Font watermarkFont = new Font(Font.HELVETICA, 40, Font.BOLD, new GrayColor(0.75f)); // Light gray watermark

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContentUnder();
        Phrase watermark = new Phrase("DRAFT", watermarkFont);

        float x = (document.right() - document.left()) / 2 + document.leftMargin();
        float y = (document.top() - document.bottom()) / 2 + document.bottomMargin();

        // Rotate watermark diagonally
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, x, y, 45);
    }
}
