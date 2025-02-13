package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.domain.Address;
import org.project.domain.Client;
import org.project.exceptions.ClientException;
import org.project.domain.CompanyType;
import org.project.repository.AddressRepository;
import org.project.repository.ClientRepository;
import org.project.repository.Repositories;

import java.util.List;

public class ClientService {

    private ClientRepository clientRepository;
    private AddressRepository addressRepository;

    public ClientService() {
        Repositories repositories = Repositories.getInstance();
        clientRepository = repositories.getClientRepository();
        addressRepository = repositories.getAddressRepository();
    }

    public List<Client> getClients() {
        DatabaseConnection connection = ConnectionFactory.getInstance().getDatabaseConnection();
        return clientRepository.getAll(connection);
    }

    public Client registerClient(int clientID, String street, String zipCode, String town, String country,String name, String vatin, int phoneNumber, String email, CompanyType type) throws ClientException {
        DatabaseConnection connection = ConnectionFactory.getInstance().getDatabaseConnection();

        if (clientRepository.getClientExists(connection, clientID)) {
            throw new ClientException("Client with ID " + clientID + " already exists.");
        }

        Address address = addressRepository.findAddress(connection, street, zipCode, town, country);
        if (address == null) {
            int id = addressRepository.getAddressCount(connection);
            address = new Address(id, street, zipCode, town, country);
            addressRepository.save(connection, address);
        }

        Client client = new Client(clientID, address, name, vatin, phoneNumber, email, type);
        boolean success = clientRepository.save(connection, client);
        if (!success) {
            return null;
        }
        return client;
    }

    public Client deleteClient (int id) throws ClientException {
        DatabaseConnection connection = ConnectionFactory.getInstance().getDatabaseConnection();

        if (!clientRepository.getClientExists(connection, id)) {
            throw new ClientException("Client with ID " + id + " not exists.");
        }

        Client client = clientRepository.getById(connection, id);
        boolean success = clientRepository.delete(connection, client);
        if (!success) {
            return null;
        }

        return client;
    }
}
