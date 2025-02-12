package org.project.ui;

import org.project.controller.ClientController;
import org.project.domain.Client;
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
        boolean register = Utils.confirm("Do you want to register a new customer?");
        if (!register) {
            return;
        }

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
    }

    private void showClients() {
        List<Client> clients = controller.getAllClients();
        System.out.println("\nClients:");
        for (Client client : clients) {
            System.out.println(" - Client ID: " + client.getId() + " | Name: " + client.getName() + " | VATIN: " + client.getVatin());
        }
    }
}
