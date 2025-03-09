package in.coempt.service.impl;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.*;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

@Service
public class HtmlToPdfService {

    private final TemplateEngine templateEngine;
    private final ServletContext servletContext;

    public HtmlToPdfService(TemplateEngine templateEngine, ServletContext servletContext) {
        this.templateEngine = templateEngine;
        this.servletContext = servletContext;
    }

    public void generatePdfFromHtml(Model model, String outputPdfPath) throws IOException {
        // ✅ Step 1: Render Thymeleaf Template into HTML
        String htmlContent = renderHtmlContentWithModel(model);

        // ✅ Step 2: Save HTML to a temporary file
        String tempHtmlPath = System.getProperty("java.io.tmpdir") + "/report_" + UUID.randomUUID() + ".html";
        Files.write(new File(tempHtmlPath).toPath(), htmlContent.getBytes());

        // ✅ Step 3: Run wkhtmltopdf to convert HTML to PDF
       // ProcessBuilder builder = new ProcessBuilder(
         //       "D:\\QPSET\\wkhtmltopdf\\bin\\wkhtmltopdf", "--enable-local-file-access", tempHtmlPath, outputPdfPath);
      //  builder.redirectErrorStream(true);

         ProcessBuilder builder = new ProcessBuilder(
               "/usr/local/bin/", "--enable-local-file-access", tempHtmlPath, outputPdfPath);
          builder.redirectErrorStream(true);
        Process process = builder.start();

        // ✅ Step 4: Wait for the process to complete
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // ✅ Step 5: Delete temporary HTML file
        new File(tempHtmlPath).delete();
    }

    private String renderHtmlContentWithModel(Model model) {

        Context context = new Context();
        context.setVariables(model.asMap());
       // context.setVariables(model);

        // ✅ Ensure CSS and image paths are absolute for correct rendering
        String baseUrl = servletContext.getRealPath("/") + "static/";
        context.setVariable("baseUrl", "file:///" + baseUrl.replace("\\", "/"));

        return templateEngine.process("pdfsample", context);
    }

    public void applyWatermark(File inputPdf, File outputPdf) throws IOException, DocumentException {
        PdfReader pdfReader = new PdfReader(new FileInputStream(inputPdf));
        PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(outputPdf));

        int totalPages = pdfReader.getNumberOfPages();
        PdfContentByte content;

        Font watermarkFont = new Font(Font.HELVETICA, 40, Font.BOLD, new GrayColor(0.75f)); // Light gray
        Phrase watermark = new Phrase("DRAFT", watermarkFont);

        for (int i = 1; i <= totalPages; i++) {
            content = pdfStamper.getUnderContent(i);
            float x = (pdfReader.getPageSize(i).getWidth()) / 2;
            float y = (pdfReader.getPageSize(i).getHeight()) / 2;

            PdfGState gState = new PdfGState();
            gState.setFillOpacity(0.3f);
            content.setGState(gState);

            ColumnText.showTextAligned(content, Element.ALIGN_CENTER, watermark, x, y, 45);
        }

        pdfStamper.close();
        pdfReader.close();
    }
}
