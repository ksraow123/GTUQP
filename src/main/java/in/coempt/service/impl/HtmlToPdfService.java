package in.coempt.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class HtmlToPdfService {

    private final TemplateEngine templateEngine;
    private final ServletContext servletContext;

    public HtmlToPdfService(TemplateEngine templateEngine, ServletContext servletContext) {
        this.templateEngine = templateEngine;
        this.servletContext = servletContext;
    }
    public void generatePdfFromHtml(Model model, String outputPdfPath) throws IOException, InterruptedException {
        // ✅ Step 1: Render Thymeleaf Template into HTML
        String htmlContent = renderHtmlContentWithModel(model);

        // ✅ Step 2: Save HTML to a temporary file
        String tempHtmlPath = System.getProperty("java.io.tmpdir") + "/report_" + UUID.randomUUID() + ".html";
        Files.write(new File(tempHtmlPath).toPath(), htmlContent.getBytes());

        // ✅ Step 3: Run wkhtmltopdf inside xvfb (Headless Mode)
        ProcessBuilder builder = new ProcessBuilder(
                "xvfb-run", "--auto-servernum", "--server-args=-screen 0 1024x768x24",
                "/usr/bin/wkhtmltopdf", "--enable-local-file-access", tempHtmlPath, outputPdfPath
        );

        builder.redirectErrorStream(true);
        Process process = builder.start();

        // ✅ Step 4: Capture and Log Process Output
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("wkhtmltopdf: " + line);
            }
        }

        int exitCode = process.waitFor();
        System.out.println("wkhtmltopdf exited with code: " + exitCode);

        if (exitCode != 0) {
            throw new IOException("wkhtmltopdf failed to generate PDF. Exit code: " + exitCode);
        }

        // ✅ Step 5: Delete Temporary HTML File
        new File(tempHtmlPath).delete();
    }

    private String renderHtmlContentWithModel(Model model) {
            Context context = new Context();
            context.setVariables(model.asMap());

            // ✅ Ensure CSS and image paths are absolute
            String baseUrl = new File(servletContext.getRealPath("/static/")).toURI().toString();
            context.setVariable("baseUrl", baseUrl);

            return templateEngine.process("pdfcopy", context);
        }

    }

