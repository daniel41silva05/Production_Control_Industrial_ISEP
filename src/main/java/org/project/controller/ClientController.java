package org.project.controller;

import org.project.model.Client;
import org.project.exceptions.ClientException;
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

    public Client getClientById(int clientID) throws ClientException {
        return clientService.getClientByID(clientID);
    }

    public Client registerClient (int clientID, String street, String zipCode, String town, String country,String name, String vatin, int phoneNumber, String email, CompanyType type) throws ClientException {
        return clientService.registerClient(clientID, street, zipCode, town, country, name, vatin, phoneNumber, email, type);
    }

    public Client deleteClient (int clientID) throws ClientException {
        return clientService.deleteClient(clientID);
    }

    public Client updateClient (Client newClient, String street, String zipCode, String town, String country, String name, String vatin, int phoneNumber, String email, CompanyType type) throws ClientException {
        return clientService.updateClient(newClient, street, zipCode, town, country, name, vatin, phoneNumber, email, type);
    }

    public List<Client> updateClientStatus () throws ClientException {
        return clientService.updateClientStatus();
    }

}
