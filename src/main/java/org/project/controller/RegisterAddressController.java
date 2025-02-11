package org.project.controller;

import org.project.data.ConnectionFactory;
import org.project.domain.Address;
import org.project.repository.AddressRepository;


public class RegisterAddressController {

    private AddressRepository addressRepository;

    public RegisterAddressController() {
        this.addressRepository = new AddressRepository();
    }

    public boolean registerAddress(String street, String zipCode, String town, String country) {
        Address address = new Address(0, street, zipCode, town, country);
        return addressRepository.save(ConnectionFactory.getInstance().getDatabaseConnection(), address);
    }
}