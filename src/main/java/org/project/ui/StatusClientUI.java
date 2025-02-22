package org.project.ui;

import org.project.controller.ClientController;
import org.project.domain.Client;
import org.project.domain.CompanyType;
import org.project.exceptions.ClientException;
import org.project.ui.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class StatusClientUI implements Runnable {

    private final ClientController controller;

    public StatusClientUI() {
        this.controller = new ClientController();
    }

    public void run() {
        try {

            List<Client> clients = controller.updateClientStatus();

            System.out.println("\nClients:");
            if (clients.isEmpty()) {
                System.out.println("No clients registered.");
            } else {
                for (Client client : clients) {
                    System.out.println(" - Client ID: " + client.getId() + " | Name: " + client.getName() + " | State: " + client.getState());
                }
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
