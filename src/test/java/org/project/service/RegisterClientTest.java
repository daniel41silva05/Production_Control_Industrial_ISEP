package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ClientException;
import org.project.model.*;
import org.project.repository.AddressRepository;
import org.project.repository.ClientRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterClientTest {

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
    public void testRegisterClient_Success() {

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

        when(clientRepository.getClientExists(connection, clientID)).thenReturn(false);
        when(addressRepository.findAddress(connection, street, zipCode, town, country)).thenReturn(null);
        when(addressRepository.getAddressCount(connection)).thenReturn(1);

        Client client = clientService.registerClient(clientID, name, vatin, street, zipCode, town, country, phoneNumber, email, type);

        assertNotNull(client);
        assertEquals(clientID, client.getId());

        verify(clientRepository, times(1)).save(eq(connection), any(Client.class));
        verify(addressRepository, times(1)).save(eq(connection), any(Address.class));
    }

    @Test
    public void testRegisterClient_ClientAlreadyExists() {

        int clientID = 1;
        when(clientRepository.getClientExists(connection, clientID)).thenReturn(true);

        assertThrows(ClientException.class, () -> {
            clientService.registerClient(clientID, "John Doe", "123456789", "Main St", "1111-111", "Springfield", "USA", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);
        });
    }

    @Test
    public void testRegisterClient_InvalidPhoneNumber() {

        int clientID = 1;
        int invalidPhoneNumber = 123;

        when(clientRepository.getClientExists(connection, clientID)).thenReturn(false);

        assertThrows(ClientException.class, () -> {
            clientService.registerClient(clientID, "John Doe", "123456789", "Main St", "1111-111", "Springfield", "USA", invalidPhoneNumber, "john.doe@example.com", CompanyType.INDIVIDUAL);
        });
    }

    @Test
    public void testRegisterClient_InvalidEmail() {
        int clientID = 1;
        String invalidEmail = "invalid-email";

        when(clientRepository.getClientExists(connection, clientID)).thenReturn(false);

        assertThrows(ClientException.class, () -> {
            clientService.registerClient(clientID, "John Doe", "123456789", "Main St", "1111-111", "Springfield", "USA", 123456789, invalidEmail, CompanyType.INDIVIDUAL);
        });
    }

    @Test
    public void testRegisterClient_InvalidZipCode() {
        int clientID = 1;
        String invalidZipCode = "111";

        when(clientRepository.getClientExists(connection, clientID)).thenReturn(false);

        assertThrows(ClientException.class, () -> {
            clientService.registerClient(clientID, "John Doe", "123456789", "Main St", invalidZipCode, "Springfield", "USA", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);
        });
    }

    @Test
    public void testRegisterClient_ExistingAddress() {
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

        Address existingAddress = new Address(5, street, zipCode, town, country);

        when(clientRepository.getClientExists(connection, clientID)).thenReturn(false);
        when(addressRepository.findAddress(connection, street, zipCode, town, country)).thenReturn(existingAddress);

        Client client = clientService.registerClient(clientID, name, vatin, street, zipCode, town, country, phoneNumber, email, type);

        assertNotNull(client);
        assertEquals(clientID, client.getId());
        assertEquals(existingAddress, client.getAddress());

        verify(addressRepository, never()).save(eq(connection), any(Address.class));
    }

}