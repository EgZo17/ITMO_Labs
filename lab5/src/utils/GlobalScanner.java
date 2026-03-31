package com.labwork.utils;

import java.util.Scanner;

public class GlobalScanner {
    private final static Scanner SCANNER = new Scanner(System.in, "cp866");

    private GlobalScanner() {};

    public static Scanner getScanner() {
        return SCANNER;
    }
}
