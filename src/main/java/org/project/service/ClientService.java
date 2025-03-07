package org.project.service;

import org.project.common.Validator;
import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.model.*;
import org.project.exceptions.ClientException;
import org.project.repository.AddressRepository;
import org.project.repository.ClientRepository;
import org.project.repository.Repositories;

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

    public Client getClientByID(int id) {
        Client client = clientRepository.getById(connection, id);

        if (client == null) {
            throw ClientException.clientNotFound(id);
        }

        return client;
    }

    public Client registerClient(int clientID, String name, String vatin, String street, String zipCode, String town, String country, int phoneNumber, String email, CompanyType type) {
        if (!Validator.isValidPhoneNumber(phoneNumber)) {
            throw ClientException.invalidPhoneNumber();
        }

        if (!Validator.isValidEmail(email)) {
            throw ClientException.invalidEmailFormat();
        }

        if (!Validator.isValidZipCode(zipCode)) {
            throw ClientException.invalidZipCode();
        }

        if (clientRepository.getClientExists(connection, clientID)) {
            throw ClientException.clientAlreadyExists(clientID);
        }

        Address address = addressRepository.findAddress(connection, street, zipCode, town, country);
        if (address == null) {
            int id = addressRepository.getAddressCount(connection);
            address = new Address(id, street, zipCode, town, country);
            addressRepository.save(connection, address);
        }

        Client client = new Client(clientID, address, name, vatin, phoneNumber, email, type);

        clientRepository.save(connection, client);

        return client;
    }

    public Client deleteClient (int id) {
        Client client = getClientByID(id);

        clientRepository.delete(connection, client);

        return client;
    }

    public Client updateClient (Client client, String name, String vatin, String street, String zipCode, String town, String country, int phoneNumber, String email, CompanyType type) {
        if (client == null) {
            return null;
        }

        if (!Validator.isValidPhoneNumber(phoneNumber)) {
            throw ClientException.invalidPhoneNumber();
        }

        if (!Validator.isValidEmail(email)) {
            throw ClientException.invalidEmailFormat();
        }

        Address address = client.getAddress();
        if (!address.getStreet().equals(street) || !address.getZipCode().equals(zipCode) || !address.getTown().equals(town) || !address.getCountry().equals(country)) {
            if (!Validator.isValidZipCode(zipCode)) {
                throw ClientException.invalidZipCode();
            }

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

        clientRepository.update(connection, client);

        return client;
    }

    public List<Client> updateClientStatus () {
        List<Client> clients = getClients();

        for (Client client : clients) {

            boolean containsActiveOrders = false;
            for (Order order : client.getOrders()) {
                if (order.getDeliveryDate().after(new Date())) {
                    containsActiveOrders = true;
                    break;
                }
            }

            if (client.getState().equals(EntityState.INACTIVE) && containsActiveOrders) {
                client.setState(EntityState.ACTIVE);
                clientRepository.updateStatus(connection, client);

            } else if (client.getState().equals(EntityState.ACTIVE) && !containsActiveOrders) {
                client.setState(EntityState.INACTIVE);
                clientRepository.updateStatus(connection, client);
            }
        }

        return clients;
    }

}
