package org.project.controller;

import org.project.domain.Client;
import org.project.domain.CompanyType;
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

    public Client registerClient (int clientID, String street, String zipCode, String town, String country,String name, String vatin, int phoneNumber, String email, CompanyType type) {
        return clientService.registerClient(clientID, street, zipCode, town, country, name, vatin, phoneNumber, email, type);
    }

}
