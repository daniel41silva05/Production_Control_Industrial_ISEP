package org.project.ui;

import org.project.controller.ClientController;
import org.project.domain.Address;
import org.project.domain.Client;
import org.project.domain.CompanyType;
import org.project.exceptions.ClientException;
import org.project.ui.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class UpdateClientUI implements Runnable {
    private final ClientController controller;

    public UpdateClientUI() {
        this.controller = new ClientController();
    }

    public void run() {
        List<Client> clients = controller.getAllClients();
        System.out.println("\nClients:");
        if (clients.isEmpty()) {
            System.out.println("No clients registered.");
            return;
        }
        for (Client client : clients) {
            System.out.println(" - Client ID: " + client.getId() + " | Name: " + client.getName() + " | VATIN: " + client.getVatin());
        }

        boolean update = Utils.confirm("Do you want to update a client's information?");
        if (!update) {
            return;
        }

        int clientID = Utils.readIntegerFromConsole("Enter Client ID to update: ");

        try {
            Client client = controller.getClientById(clientID);
            if (client == null) {
                System.out.println("\nClient not found.");
                return;
            }

            showClient(client);

            if (Utils.confirm("Do you want to update the Name?")) {
                String newName = Utils.readLineFromConsole("Enter new Name: ");
                client.setName(newName);
            }

            if (Utils.confirm("Do you want to update the VATIN?")) {
                String newVatin = Utils.readLineFromConsole("Enter new VATIN: ");
                client.setVatin(newVatin);
            }

            if (Utils.confirm("Do you want to update the Email?")) {
                String newEmail = Utils.readLineFromConsole("Enter new Email: ");
                client.setEmail(newEmail);
            }

            if (Utils.confirm("Do you want to update the Phone Number?")) {
                int newPhone = Utils.readIntegerFromConsole("Enter new Phone Number: ");
                client.setPhoneNumber(newPhone);
            }

            if (Utils.confirm("Do you want to update the Type?")) {
                System.out.println();
                List<CompanyType> options = Arrays.asList(CompanyType.values());
                CompanyType newType = (CompanyType) Utils.showAndSelectOne(options, "Select Company Type:");
                client.setType(newType);
            }

            if (Utils.confirm("Do you want to update the Address?")) {
                Address address = client.getAddress();

                if (Utils.confirm("Do you want to update the Street?")) {
                    String newStreet = Utils.readLineFromConsole("Enter new Street: ");
                    address.setStreet(newStreet);
                }
                if (Utils.confirm("Do you want to update the Zip Code?")) {
                    String newZipCode = Utils.readLineFromConsole("Enter new Zip Code: ");
                    address.setZipCode(newZipCode);
                }
                if (Utils.confirm("Do you want to update the Town?")) {
                    String newTown = Utils.readLineFromConsole("Enter new Town: ");
                    address.setTown(newTown);
                }
                if (Utils.confirm("Do you want to update the Country?")) {
                    String newCountry = Utils.readLineFromConsole("Enter new Country: ");
                    address.setCountry(newCountry);
                }
            }

            Client newClient = controller.updateClient(client);
            if (newClient != null) {
                System.out.println("\nClient updated successfully.");
                showClient(newClient);
            } else {
                System.out.println("\nClient update failed.");
            }
        } catch (ClientException e) {
            System.out.println("\nError: " + e.getMessage());
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
