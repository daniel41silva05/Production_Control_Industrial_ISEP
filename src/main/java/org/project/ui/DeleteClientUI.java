package org.project.ui;

import org.project.controller.ClientController;
import org.project.exceptions.DatabaseException;
import org.project.model.Client;
import org.project.exceptions.ClientException;
import org.project.ui.utils.Utils;
import java.util.List;

public class DeleteClientUI implements Runnable {

    private final ClientController controller;

    public DeleteClientUI() {
        this.controller = new ClientController();
    }

    public void run() {
        try {
            showClients(controller.getAllClients());

            boolean delete = Utils.confirm("Do you want to delete a client?");
            if (!delete) {
                return;
            }

            int clientID = Utils.readIntegerFromConsole("Enter Client ID: ");

            Client client = controller.deleteClient(clientID);
            if (client == null) {
                System.out.println("\nFailed to delete client.");
            } else {
                System.out.println("\nClient deleted successfully.");
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

}
