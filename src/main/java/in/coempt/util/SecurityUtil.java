package in.coempt.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Authentication getLoggedUserDetails() {
        return SecurityContextHolder.getContext().getAuthentication();

    }
}
