package in.coempt.util;

public class MaskingUtil {

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return "*@".concat(domain);
        }

        StringBuilder masked = new StringBuilder();
        masked.append(username.substring(0, 2));
        for (int i = 2; i < username.length(); i++) {
            masked.append("*");
        }
        masked.append("@").append(domain);

        return masked.toString();
    }

    public static String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 5) return mobile;

        return mobile.substring(0, 2) + "*****" + mobile.substring(mobile.length() - 3);
    }
}
