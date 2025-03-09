package in.coempt.service.impl;

import in.coempt.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class PdfServiceImpl implements PdfService {
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${wkhtmltopdf.path}") // Path to wkhtmltopdf executable
    private String wkhtmltopdfPath;

    /**
     * Generate PDF from Thymeleaf template.
     *
     * @param templateName Name of the Thymeleaf template (e.g., "pdfsample.html")
     * @param data         Data to populate the template
     * @param outputPath   Path to save the generated PDF
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the process is interrupted
     */
    public void generatePdfFromTemplate(String templateName, Map<String, Object> data, String outputPath)
            throws IOException, InterruptedException {
        // Render HTML from Thymeleaf template
        Context context = new Context();
        context.setVariables(data);
        String htmlContent = templateEngine.process(templateName, context);

        // Save HTML to a temporary file
        String tempHtmlPath = "temp.html";
        Files.write(Paths.get(tempHtmlPath), htmlContent.getBytes());

        // Generate PDF using wkhtmltopdf
        String command = wkhtmltopdfPath + " " + tempHtmlPath + " " + outputPath;
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();

        // Delete the temporary HTML file
        Files.deleteIfExists(Paths.get(tempHtmlPath));
    }

}
