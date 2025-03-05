package org.project.ui;

import org.project.controller.ClientController;
import org.project.exceptions.ClientException;
import org.project.exceptions.DatabaseException;
import org.project.model.Client;
import org.project.model.CompanyType;
import org.project.ui.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class UpdateClientUI implements Runnable {
    private final ClientController controller;

    public UpdateClientUI() {
        this.controller = new ClientController();
    }

    public void run() {
        try {
            showClients(controller.getAllClients());

            boolean update = Utils.confirm("Do you want to update a client's information?");
            if (!update) {
                return;
            }

            int clientID = Utils.readIntegerFromConsole("Enter Client ID to update: ");

            Client client = controller.getClientById(clientID);

            showClient(client);

            String street = client.getAddress().getStreet();
            String zipCode = client.getAddress().getZipCode();
            String town = client.getAddress().getTown();
            String country = client.getAddress().getCountry();
            String name = client.getName();
            String vatin = client.getVatin();
            int phoneNumber = client.getPhoneNumber();
            String email = client.getEmail();
            CompanyType type = client.getType();

            if (Utils.confirm("Do you want to update the Name?")) {
                name = Utils.readLineFromConsole("Enter new Name: ");
            }

            if (Utils.confirm("Do you want to update the VATIN?")) {
                vatin = Utils.readLineFromConsole("Enter new VATIN: ");
            }

            if (Utils.confirm("Do you want to update the Email?")) {
                email = Utils.readLineFromConsole("Enter new Email: ");
            }

            if (Utils.confirm("Do you want to update the Phone Number?")) {
                phoneNumber = Utils.readIntegerFromConsole("Enter new Phone Number: ");
            }

            if (Utils.confirm("Do you want to update the Type?")) {
                System.out.println();
                List<CompanyType> options = Arrays.asList(CompanyType.values());
                type = (CompanyType) Utils.showAndSelectOne(options, "Select Company Type:");
            }

            if (Utils.confirm("Do you want to update the Address?")) {

                if (Utils.confirm("Do you want to update the Street?")) {
                    street = Utils.readLineFromConsole("Enter new Street: ");
                }
                if (Utils.confirm("Do you want to update the Zip Code?")) {
                    zipCode = Utils.readLineFromConsole("Enter new Zip Code: ");
                }
                if (Utils.confirm("Do you want to update the Town?")) {
                    town = Utils.readLineFromConsole("Enter new Town: ");
                }
                if (Utils.confirm("Do you want to update the Country?")) {
                    country = Utils.readLineFromConsole("Enter new Country: ");
                }
            }

            Client newClient = controller.updateClient(client, street, zipCode, town, country, name, vatin, phoneNumber, email, type);
            if (newClient != null) {
                System.out.println("\nClient updated successfully.");
                showClient(newClient);
            } else {
                System.out.println("\nClient update failed.");
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
