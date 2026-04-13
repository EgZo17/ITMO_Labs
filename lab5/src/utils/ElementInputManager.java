package com.labwork.utils;

import java.util.Arrays;
import java.util.Scanner;
import com.labwork.collection.LabCollection;
import com.labwork.data.*;
import com.labwork.enums.*;

public class ElementInputManager {
    public static LabWork readElement(Scanner scanner) {
        int id;
        String name;
        double coordX;
        int coordY;
        Float minimalPoint;
        String difficultyName;
        Difficulty difficulty;
        String authorName;
        Double authorHeight;
        String authorEyeColorName;
        Color authorEyeColor;
        Integer locationX;
        double locationY;
        Double locationZ;

        LabCollection labCollection = LabCollection.getInstance();

        System.out.println("=== IN readElement ===");
        System.out.println("Scanner = " + scanner);
        System.out.println("Scanner class = " + scanner.getClass());

        id = labCollection.getNextId();
        name = readString("Please, enter this laboratory work's name: ",
                input -> !input.trim().isEmpty(),
                "Name can not be null nor empty, try again.", false, scanner);
        coordX = readDouble("Please, enter the X coordinate of this laboratory work (double): ",
                value -> true,
                "---ERROR---", scanner);
        coordY = readInt("Please, enter the Y coordinate of this laboratory work (int): ", 
                value -> true,
                "---ERROR---", scanner);
        minimalPoint = readFloat("Please, enter the minimal point this laboratory work to get (float): ", 
                value -> value > 0,
                "Minimal point must be a positive number, try again.", scanner);
        difficultyName = readString("Please, enter difficulty of this laboratory work [HARD, HOPELESS, TERRIBLE] (skipable): ", 
                input -> Arrays.asList("HARD", "HOPELESS", "TERRIBLE").contains(input),
                "Difficulty level you entered is not in the list, try again.", true, scanner);
        if (!(difficultyName == null)) {
            difficulty = Difficulty.valueOf(difficultyName);
        }
        else {
            difficulty = null;
        }
        authorName = readString("Please, enter the name of author of this laboratory work: ",
                input -> !input.trim().isEmpty(),
                "Name can not be null nor empty, try again.", false, scanner);
        authorHeight = readDouble("Please, enter the height of laboratory work's author (double): ", 
                value -> value > 0, "Height must be a positive number, try again.", scanner);
        authorEyeColorName = readString("Please, enter the color of eyes of this laboratory work's author [RED, BLACK, ORANGE]: ", 
                input -> Arrays.asList("RED", "BLACK", "ORANGE").contains(input),
                "The color of eyes you entered is not in the list, try again.", false, scanner);
        authorEyeColor = Color.valueOf(authorEyeColorName);
        locationX = readInt("Please, enter the X coordinate of author's location (int): ", 
                value -> true,
                "---ERROR---", scanner);
        locationY = readDouble("Please, enter the Y coordinate of author's location (double): ", 
                value -> true,
                "---ERROR---", scanner);
        locationZ = readDouble("Please, enter the Z coordinate of author's location (double): ", 
                value -> true,
                "---ERROR---", scanner);
        
        Coordinates coordinates = new Coordinates(coordX, coordY);
        Location location = new Location(locationX, locationY, locationZ);
        Person author = new Person(authorName, authorHeight, authorEyeColor, location);
        LabWork result = new LabWork(id, name, coordinates, minimalPoint, difficulty, author);
        return result;
    }

    private static String readString(String prompt, java.util.function.Predicate<String> validator, String errorMessage, boolean allowNull, Scanner reader) {
        while (true) {
            System.out.print(prompt);
            String input = reader.nextLine().trim();
            if (input == null || input.trim().isEmpty()) {
                if (allowNull == true) {
                    return null;
                }
            }
            if (validator.test(input)) {
                return input;
            }
            System.out.println("Error: " + errorMessage);
        }
    }

    private static int readInt(String prompt, java.util.function.IntPredicate validator, String errorMessage, Scanner reader) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(reader.nextLine());
                if (validator.test(value)) {
                    return value;
                }
                System.out.println("Error: " + errorMessage);
            } catch (NumberFormatException e) {
                System.out.println("Error: Integer was expected.");
            }
        }
    }

    private static double readDouble(String prompt, java.util.function.DoublePredicate validator, String errorMessage, Scanner reader) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(reader.nextLine());
                if (validator.test(value)) {
                    return value;
                }
                System.out.println("Error: " + errorMessage);
            } catch (NumberFormatException e) {
                System.out.println("Error: Double was expected.");
            }
        }
    }

    private static float readFloat(String prompt, java.util.function.DoublePredicate validator, String errorMessage, Scanner reader) {
        while (true) {
            System.out.print(prompt);
            try {
                float value = Float.parseFloat(reader.nextLine());
                if (validator.test(value)) {
                    return value;
                }
                System.out.println("Error: " + errorMessage);
            } catch (NumberFormatException e) {
                System.out.println("Error: Float was expected.");
            }
        }
    }
}
