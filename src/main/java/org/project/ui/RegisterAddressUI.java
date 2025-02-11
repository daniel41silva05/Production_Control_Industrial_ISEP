package org.project.ui;

import org.project.controller.RegisterAddressController;

import java.util.Scanner;

public class RegisterAddressUI implements Runnable {

    private final RegisterAddressController controller;

    public RegisterAddressUI() {
        this.controller = new RegisterAddressController();
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n--- Register Address ---");

        // Coletar dados do endereço
        System.out.print("Enter street: ");
        String street = scanner.nextLine().trim();

        System.out.print("Enter zip code: ");
        String zipCode = scanner.nextLine().trim();

        System.out.print("Enter town: ");
        String town = scanner.nextLine().trim();

        System.out.print("Enter country: ");
        String country = scanner.nextLine().trim();

        // Registrar o endereço
        boolean success = controller.registerAddress(street, zipCode, town, country);

        // Exibir resultado
        if (success) {
            System.out.println("Address registered successfully!");
        } else {
            System.out.println("Failed to register address.");
        }
    }
}