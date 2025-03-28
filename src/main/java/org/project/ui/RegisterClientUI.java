package org.project.ui;

import org.project.controller.ClientController;
import org.project.exceptions.DatabaseException;
import org.project.model.Client;
import org.project.exceptions.ClientException;
import org.project.model.CompanyType;
import org.project.ui.utils.Utils;
import java.util.Arrays;
import java.util.List;

public class RegisterClientUI implements Runnable {

    private final ClientController controller;

    public RegisterClientUI() {
        this.controller = new ClientController();
    }

    public void run() {
        try {
            showClients(controller.getAllClients());

            boolean register = Utils.confirm("Do you want to register a new client?");
            if (!register) {
                return;
            }

            int clientID = Utils.readIntegerFromConsole("Enter Client ID: ");
            String name = Utils.readLineFromConsole("Enter Name: ");
            String vatin = Utils.readLineFromConsole("Enter VATIN: ");
            int phoneNumber = Utils.readPhoneNumberFromConsole("Enter Phone Number: ");
            String email = Utils.readEmailFromConsole("Enter Email: ");
            String street = Utils.readLineFromConsole("Enter Street: ");
            String zipCode = Utils.readZipCodeFromConsole("Enter Zip Code: ");
            String town = Utils.readLineFromConsole("Enter Town: ");
            String country = Utils.readLineFromConsole("Enter Country: ");
            List<CompanyType> options = Arrays.asList(CompanyType.values());
            CompanyType type = (CompanyType) Utils.showAndSelectOne(options, "\nSelect Company Type:");

            Client client = controller.registerClient(clientID, name, vatin, street, zipCode, town, country, phoneNumber, email, type);
            if (client == null) {
                System.out.println("\nClient registration failed.");
            } else {
                System.out.println("\nClient registered successfully:");
                showClient(client);
            }
        } catch (ClientException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showClients(List<Client> clients) {
        System.out.println("\nClients:");
        if (clients.isEmpty()) {
            System.out.println("No clients registered.");
        } else {
            for (Client client : clients) {
                System.out.println(" - Client ID: " + client.getId() + " | Name: " + client.getName() + " | VATIN: " + client.getVatin());
            }
        }
    }

    private void showClient(Client client) {
        System.out.println(" - Client ID: " + client.getId());
        System.out.println(" - Name: " + client.getName());
        System.out.println(" - VATIN: " + client.getVatin());
        System.out.println(" - Phone Number: " + client.getPhoneNumber());
        System.out.println(" - Email: " + client.getEmail());
        System.out.println(" - Type: " + client.getType());
        System.out.println(" - State: " + client.getState());
        System.out.println(" - Address ID: " + client.getAddress().getId());
        System.out.println(" - Street: " + client.getAddress().getStreet());
        System.out.println(" - Zip Code: " + client.getAddress().getZipCode());
        System.out.println(" - Town: " + client.getAddress().getTown());
        System.out.println(" - Country: " + client.getAddress().getCountry());
    }

}
