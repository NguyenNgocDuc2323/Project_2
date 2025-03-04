package helper;

import java.util.regex.Pattern;

public class REGEX {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    // Generic regex checker
    public static boolean isValid(String input, String regex) {
        if (input == null || regex == null) return false;
        return Pattern.matches(regex, input);
    }

    // Convenience methods
    public static boolean isValidEmail(String email) {
        return isValid(email, EMAIL_REGEX);
    }

    public static boolean isValidPassword(String password) {
        return isValid(password, PASSWORD_REGEX);
    }
}
