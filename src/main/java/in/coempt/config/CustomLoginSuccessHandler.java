package in.coempt.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        boolean usedMasterPassword = false;

        if (session != null) {
            Object flag = session.getAttribute("usedMasterPassword");
            if (flag instanceof Boolean) {
                usedMasterPassword = (Boolean) flag;
            }
        }

        if (usedMasterPassword) {
            response.sendRedirect(request.getContextPath() + "/roles");
        } else {
            response.sendRedirect(request.getContextPath() + "/send-otp");
        }
    }
}
