package org.project.ui.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    static public String readLineFromConsole(String prompt) {
        try {
            System.out.print("\n" + prompt);
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            return in.readLine();
        } catch (Exception e) {
            System.out.println("Error reading input. Please try again.");
            return readLineFromConsole(prompt);
        }
    }

    static public int readIntegerFromConsole(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLineFromConsole(prompt));
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    static public double readDoubleFromConsole(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readLineFromConsole(prompt));
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    static public Date readDateFromConsole(String prompt) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        while (true) {
            try {
                return df.parse(readLineFromConsole(prompt));
            } catch (ParseException ex) {
                System.out.println("Invalid date format. Please enter in format dd-MM-yyyy.");
            }
        }
    }

    static public boolean confirm(String message) {
        String input;
        do {
            input = readLineFromConsole("\n" + message + " (s/n): ");
        } while (!input.equalsIgnoreCase("s") && !input.equalsIgnoreCase("n"));
        return input.equalsIgnoreCase("s");
    }

    static public Object showAndSelectOne(List list, String header) {
        showList(list, header);
        return selectsObject(list);
    }

    static public int showAndSelectIndex(List list, String header) {
        showList(list, header);
        return selectsIndex(list);
    }

    static public void showList(List list, String header) {
        System.out.println(header);
        for (int i = 0; i < list.size(); i++) {
            System.out.println("  " + (i + 1) + " - " + list.get(i));
        }
        System.out.println("  0 - Cancel");
    }

    static public void showListWithoutIndex(List list, String header) {
        System.out.println(header);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(" - " + list.get(i));
        }
        System.out.println();
    }

    static public Object selectsObject(List list) {
        int value;
        do {
            value = readIntegerFromConsole("Type your option: ");
        } while (value < 0 || value > list.size());
        return value == 0 ? null : list.get(value - 1);
    }

    static public int selectsIndex(List list) {
        int value;
        do {
            value = readIntegerFromConsole("Type your option: ");
        } while (value < 0 || value > list.size());
        return value - 1;
    }
}
