package org.project.controller;

import org.project.data.ConnectionFactory;
import org.project.domain.Address;
import org.project.repository.AddressRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class RegisterAddressController {

    private AddressRepository addressRepository;

    public RegisterAddressController() {
        this.addressRepository = new AddressRepository();
    }

    public boolean registerAddress(String street, String zipCode, String town, String country) {
        try (Connection connection = ConnectionFactory.getInstance().getDatabaseConnection()) {
            Address address = new Address(0, street, zipCode, town, country); // AddressID = 0 (gerado automaticamente)
            return addressRepository.save(connection, address);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}