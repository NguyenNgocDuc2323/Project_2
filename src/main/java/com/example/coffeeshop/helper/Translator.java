package com.example.coffeeshop.helper;

import java.util.Locale;
import java.util.ResourceBundle;

public class Translator {
    private static ResourceBundle bundle;
    private static Locale locale;
    static {
        locale = new Locale("en", "US");
        loadBundle();
    }

    private static void loadBundle() {
        bundle = ResourceBundle.getBundle("com/example/coffeeshop/language/language", locale);
    }

    public static ResourceBundle getResourceBundle() {
        return bundle;
    }

    public static Locale getLocale() {
        return locale;
    }

    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        bundle = ResourceBundle.getBundle("com/example/coffeeshop/language/language", locale);
    }

    public static String translate(String key) {
        return bundle.getString(key);
    }
    public static void toggleLanguage() {
        if (locale.getLanguage().equals("vi")) {
            setLocale(new Locale("en", "US"));
        } else {
            setLocale(new Locale("vi", "VN"));
        }
    }
}
