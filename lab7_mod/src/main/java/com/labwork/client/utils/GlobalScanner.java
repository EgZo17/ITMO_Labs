package com.labwork.client.utils;

import java.util.Scanner;

/**
 * Глобальный класс-обёртка для Scanner.
 * Позволяет подменять источник ввода при выполнении скриптов.
 */

public class GlobalScanner {
    private static Scanner SCANNER = new Scanner(System.in, "cp866");

    private GlobalScanner() {};

    public static Scanner getScanner() {
        return SCANNER;
    }

    public static void setScanner(Scanner tempScaner) {
        SCANNER = tempScaner;
    }
}
