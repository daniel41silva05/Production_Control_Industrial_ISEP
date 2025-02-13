package org.project.ui;

import org.project.controller.ClientController;
import org.project.domain.Client;
import org.project.exceptions.ClientException;
import org.project.ui.utils.Utils;
import java.util.List;

public class DeleteClientUI implements Runnable {

    private final ClientController controller;

    public DeleteClientUI() {
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

        boolean delete = Utils.confirm("Do you want to delete a client?");
        if (!delete) {
            return;
        }

        int clientID = Utils.readIntegerFromConsole("Enter Client ID: ");

        try {
            Client client = controller.deleteClient(clientID);
            if (client == null) {
                System.out.println("\nFailed to delete client.");
            } else {
                System.out.println("\nClient deleted successfully.");
            }
        } catch (ClientException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nFailed to delete client.");
        }
    }

}
