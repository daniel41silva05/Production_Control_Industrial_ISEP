package org.project.ui;

import org.project.controller.ClientController;
import org.project.exceptions.DatabaseException;
import org.project.model.Client;
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

        } catch (DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

}
