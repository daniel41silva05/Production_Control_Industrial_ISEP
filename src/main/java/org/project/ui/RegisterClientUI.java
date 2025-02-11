package org.project.ui;

import org.project.controller.RegisterClientController;

import java.util.Scanner;

public class RegisterClientUI implements Runnable {

    private final RegisterClientController controller;

    public RegisterClientUI() {
        this.controller = new RegisterClientController();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String key = null;

        while (key == null || key.trim().isEmpty()) {
            System.out.print("Enter name/id of an operation/material: ");
            key = scanner.nextLine().trim();
        }
    }

}
