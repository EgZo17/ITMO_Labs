package com.labwork.client.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;

public class LocaleManager {
    private static Locale currentLocale = new Locale("ru");
    private static ResourceBundle bundle = ResourceBundle.getBundle("messages", currentLocale);
    private static final List<LocaleChangeListener> listeners = new ArrayList<>();

    public interface LocaleChangeListener {
        void onLocaleChanged(Locale newLocale);
    }

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        bundle = ResourceBundle.getBundle("messages", currentLocale);
        for (LocaleChangeListener l : listeners) {
            l.onLocaleChanged(locale);
        }
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static String t(String key) {
        return bundle.getString(key);
    }

    public static String t(String key, Object... args) {
        return MessageFormat.format(bundle.getString(key), args);
    }

    public static void addListener(LocaleChangeListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(LocaleChangeListener listener) {
        listeners.remove(listener);
    }

    public static List<Locale> getAvailableLocales() {
        List<Locale> list = new ArrayList<>();
        list.add(new Locale("ru"));
        list.add(new Locale("be"));
        list.add(new Locale("bg"));
        list.add(new Locale("es", "EC"));
        return list;
    }
}
