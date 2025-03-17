package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ClientException;
import org.project.model.Address;
import org.project.model.Client;
import org.project.model.CompanyType;
import org.project.repository.AddressRepository;
import org.project.repository.ClientRepository;
import org.project.service.ClientService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UpdateClientTest {

    private ClientService clientService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AddressRepository addressRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        clientService = new ClientService(connection, clientRepository, addressRepository);
    }

    @Test
    public void testUpdateClient_Success() {
        int clientID = 1;
        String name = "John Doe";
        String vatin = "123456789";
        String street = "Main St";
        String zipCode = "1111-111";
        String town = "Springfield";
        String country = "USA";
        int phoneNumber = 123456789;
        String email = "john.doe@example.com";
        CompanyType type = CompanyType.INDIVIDUAL;

        Client existingClient = new Client(clientID, null, "Old Name", "987654321", 987654321, "old.email@example.com", CompanyType.INDIVIDUAL);
        Address existingAddress = new Address(1, "Old St", "9999-999", "Old Town", "Old Country");
        existingClient.setAddress(existingAddress);

        when(clientRepository.getById(connection, clientID)).thenReturn(existingClient);
        when(addressRepository.findAddress(connection, street, zipCode, town, country)).thenReturn(null);
        when(addressRepository.getAddressCount(connection)).thenReturn(2);

        Client updatedClient = clientService.updateClient(existingClient, name, vatin, street, zipCode, town, country, phoneNumber, email, type);

        assertNotNull(updatedClient);
        assertEquals(name, updatedClient.getName());
        assertEquals(vatin, updatedClient.getVatin());
        assertEquals(phoneNumber, updatedClient.getPhoneNumber());
        assertEquals(email, updatedClient.getEmail());
        assertEquals(type, updatedClient.getType());

        verify(clientRepository, times(1)).update(eq(connection), any(Client.class));
        verify(addressRepository, times(1)).save(eq(connection), any(Address.class));
    }

    @Test
    public void testUpdateClient_ClientNotFound() {
        assertNull(clientService.updateClient(null, "John Doe", "123456789", "Main St", "9999-999", "Springfield", "USA", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL));
    }

    @Test
    public void testUpdateClient_InvalidPhoneNumber() {
        int clientID = 1;
        int invalidPhoneNumber = 123;

        Client existingClient = new Client(clientID, new Address(1, "Old St", "9999-999", "Old Town", "Old Country"), "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);

        when(clientRepository.getById(connection, clientID)).thenReturn(existingClient);

        assertThrows(ClientException.class, () -> {
            clientService.updateClient(existingClient, "John Doe", "123456789", "Main St", "1111-111", "Springfield", "USA", invalidPhoneNumber, "john.doe@example.com", CompanyType.INDIVIDUAL);
        });
    }

    @Test
    public void testUpdateClient_InvalidEmail() {
        int clientID = 1;
        String invalidEmail = "invalid-email";

        Client existingClient = new Client(clientID, new Address(1, "Old St", "9999-999", "Old Town", "Old Country"), "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);

        when(clientRepository.getById(connection, clientID)).thenReturn(existingClient);

        assertThrows(ClientException.class, () -> {
            clientService.updateClient(existingClient, "John Doe", "123456789", "Main St", "1111-111", "Springfield", "USA", 123456789, invalidEmail, CompanyType.INDIVIDUAL);
        });
    }

    @Test
    public void testUpdateClient_InvalidZipCode() {
        int clientID = 1;
        String invalidZipCode = "111";

        Client existingClient = new Client(clientID, new Address(1, "Old St", "9999-999", "Old Town", "Old Country"), "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);

        when(clientRepository.getById(connection, clientID)).thenReturn(existingClient);

        assertThrows(ClientException.class, () -> {
            clientService.updateClient(existingClient, "John Doe", "123456789", "Main St", invalidZipCode, "Springfield", "USA", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);
        });
    }

    @Test
    public void testUpdateClient_ExistingAddress() {
        int clientID = 1;
        String name = "John Doe";
        String vatin = "123456789";
        String street = "Main St";
        String zipCode = "1111-111";
        String town = "Springfield";
        String country = "USA";
        int phoneNumber = 123456789;
        String email = "john.doe@example.com";
        CompanyType type = CompanyType.INDIVIDUAL;

        Client existingClient = new Client(clientID, new Address(1, "Old St", "9999-999", "Old Town", "Old Country"), "Old Name", "987654321", 987654321, "old.email@example.com", CompanyType.INDIVIDUAL);

        Address newAddress = new Address(2, street, zipCode, town, country);

        when(clientRepository.getById(connection, clientID)).thenReturn(existingClient);
        when(addressRepository.findAddress(connection, street, zipCode, town, country)).thenReturn(newAddress);

        Client updatedClient = clientService.updateClient(existingClient, name, vatin, street, zipCode, town, country, phoneNumber, email, type);

        assertNotNull(updatedClient);
        assertEquals(newAddress, updatedClient.getAddress());

        verify(addressRepository, never()).save(eq(connection), any(Address.class));
    }

}