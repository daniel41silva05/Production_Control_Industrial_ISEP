package org.project.controller;

import org.project.model.Client;
import org.project.model.CompanyType;
import org.project.service.ClientService;

import java.util.List;

public class ClientController {

    private ClientService clientService;

    public ClientController() {
        clientService = new ClientService();
    }

    public List<Client> getAllClients() {
        return clientService.getClients();
    }

    public Client getClientById(int clientID) {
        return clientService.getClientByID(clientID);
    }

    public Client registerClient (int clientID, String name, String vatin, String street, String zipCode, String town, String country, int phoneNumber, String email, CompanyType type) {
        return clientService.registerClient(clientID, name, vatin, street, zipCode, town, country, phoneNumber, email, type);
    }

    public Client deleteClient (int clientID) {
        return clientService.deleteClient(clientID);
    }

    public Client updateClient (Client newClient, String street, String zipCode, String town, String country, String name, String vatin, int phoneNumber, String email, CompanyType type) {
        return clientService.updateClient(newClient, street, zipCode, town, country, name, vatin, phoneNumber, email, type);
    }

    public List<Client> updateClientStatus () {
        return clientService.updateClientStatus();
    }

}
