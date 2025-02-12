package org.project.ui;

import org.project.controller.ClientController;
import org.project.domain.Client;
import org.project.domain.ClientException;
import org.project.domain.CompanyType;
import org.project.ui.utils.Utils;
import java.util.Arrays;
import java.util.List;

public class RegisterClientUI implements Runnable {

    private final ClientController controller;

    public RegisterClientUI() {
        this.controller = new ClientController();
    }

    public void run() {
        showClients();
        boolean register = Utils.confirm("Do you want to register a new client?");
        if (!register) {
            return;
        }

        try {
            int clientID = Utils.readIntegerFromConsole("Enter Client ID: ");
            String name = Utils.readLineFromConsole("Enter Name: ");
            String vatin = Utils.readLineFromConsole("Enter VATIN: ");
            int phoneNumber = Utils.readIntegerFromConsole("Enter Phone Number: ");
            String email = Utils.readLineFromConsole("Enter Email: ");
            String street = Utils.readLineFromConsole("Enter Street: ");
            String zipCode = Utils.readLineFromConsole("Enter Zip Code: ");
            String town = Utils.readLineFromConsole("Enter Town: ");
            String country = Utils.readLineFromConsole("Enter Country: ");
            System.out.println();
            List<CompanyType> options = Arrays.asList(CompanyType.values());
            CompanyType type = (CompanyType) Utils.showAndSelectOne(options, "Select Company Type:");

            Client client = controller.registerClient(clientID, street, zipCode, town, country, name, vatin, phoneNumber, email, type);
            if (client == null) {
                System.out.println("\nClient registration failed.");
            } else {
                System.out.println("\nClient registered successfully.");
            }
        } catch (ClientException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nClient registration failed.");
        }
    }

    private void showClients() {
        List<Client> clients = controller.getAllClients();
        System.out.println("\nClients:");
        if (clients.isEmpty()) {
            System.out.println("No clients registered.");
        } else {
            for (Client client : clients) {
                System.out.println(" - Client ID: " + client.getId() + " | Name: " + client.getName() + " | VATIN: " + client.getVatin());
            }
        }
    }

}
