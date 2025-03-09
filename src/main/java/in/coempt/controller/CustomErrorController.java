package in.coempt.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Extract error details
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
        String requestPath = (String) request.getAttribute("javax.servlet.error.request_uri");

        // Add attributes to model
        model.addAttribute("status", statusCode);
        model.addAttribute("error", (errorMessage != null) ? errorMessage : "Unexpected Error");
        model.addAttribute("path", requestPath);
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error"; // This should match error.html in your templates
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

