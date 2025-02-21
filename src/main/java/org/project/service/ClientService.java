package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.domain.*;
import org.project.exceptions.ClientException;
import org.project.repository.AddressRepository;
import org.project.repository.ClientRepository;
import org.project.repository.Repositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientService {

    private DatabaseConnection connection;
    private ClientRepository clientRepository;
    private AddressRepository addressRepository;

    public ClientService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        clientRepository = repositories.getClientRepository();
        addressRepository = repositories.getAddressRepository();
    }

    public List<Client> getClients() {
        return clientRepository.getAll(connection);
    }

    public Client registerClient(int clientID, String street, String zipCode, String town, String country, String name, String vatin, int phoneNumber, String email, CompanyType type) throws ClientException {
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

    public Client getClientByID(int id) throws ClientException {
        if (!clientRepository.getClientExists(connection, id)) {
            throw new ClientException("Client with ID " + id + " not exists.");
        }

        return clientRepository.getById(connection, id);
    }

    public Client updateClient (Client client, String street, String zipCode, String town, String country, String name, String vatin, int phoneNumber, String email, CompanyType type) {
        Address address = client.getAddress();
        if (!address.getStreet().equals(street) || !address.getZipCode().equals(zipCode) || !address.getTown().equals(town) || !address.getCountry().equals(country)) {
            address = addressRepository.findAddress(connection, street, zipCode, town, country);
            if (address == null) {
                int id = addressRepository.getAddressCount(connection);
                address = new Address(id, street, zipCode, town, country);
                addressRepository.save(connection, address);
            }
        }

        client.setAddress(address);
        client.setName(name);
        client.setVatin(vatin);
        client.setPhoneNumber(phoneNumber);
        client.setEmail(email);
        client.setType(type);

        boolean success = clientRepository.update(connection, client);
        if (!success) {
            return null;
        }

        return client;
    }

    public List<Client> updateClientStatus () throws ClientException {
        List<Client> clients = clientRepository.getAll(connection);

        for (Client client : clients) {

            List<Order> activeOrders = new ArrayList<>();
            for (Order order : client.getOrders()) {
                if (order.getDeliveryDate().after(new Date())) {
                    activeOrders.add(order);
                }
            }

            if (client.getState().equals(State.INACTIVE) && !activeOrders.isEmpty()) {
                client.setState(State.ACTIVE);
                boolean success = clientRepository.updateStatus(connection, client);
                if (!success) {
                    throw new ClientException("Problems updating client status.");
                }
            } else if (client.getState().equals(State.ACTIVE) && activeOrders.isEmpty()) {
                client.setState(State.INACTIVE);
                boolean success = clientRepository.updateStatus(connection, client);
                if (!success) {
                    throw new ClientException("Problems updating client status.");
                }
            }
        }

        return clients;
    }
}
