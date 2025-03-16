package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.model.*;
import org.project.repository.AddressRepository;
import org.project.repository.ClientRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UpdateClientStatusTest {

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
    public void testUpdateClientStatus_ActivateClient() {
        Address address = new Address(1, "Main St", "1111-111", "Springfield", "USA");
        Client client = new Client(1, address, "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);
        Order order = new Order(1, address, new Date(System.currentTimeMillis() - 10000), new Date(System.currentTimeMillis() + 10000), 100.0, ProcessState.PENDING);
        client.getOrders().add(order);

        List<Client> clients = new ArrayList<>();
        clients.add(client);

        when(clientRepository.getAll(connection)).thenReturn(clients);

        clientService.updateClientStatus();

        assertEquals(EntityState.ACTIVE, client.getState());
        verify(clientRepository, times(1)).updateStatus(eq(connection), eq(client));
    }

    @Test
    public void testUpdateClientStatus_DeactivateClient() {
        Address address = new Address(1, "Main St", "1111-111", "Springfield", "USA");
        Client client = new Client(1, address, "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);
        client.setState(EntityState.ACTIVE);
        Order order = new Order(1, address, new Date(System.currentTimeMillis() - 20000), new Date(System.currentTimeMillis() - 10000), 100.0, ProcessState.PENDING);
        client.getOrders().add(order);

        List<Client> clients = new ArrayList<>();
        clients.add(client);

        when(clientRepository.getAll(connection)).thenReturn(clients);

        clientService.updateClientStatus();

        assertEquals(EntityState.INACTIVE, client.getState());
        verify(clientRepository, times(1)).updateStatus(eq(connection), eq(client));
    }

    @Test
    public void testUpdateClientStatus_NoOrders() {
        Address address = new Address(1, "Main St", "1111-111", "Springfield", "USA");
        Client client = new Client(1, address, "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);

        List<Client> clients = new ArrayList<>();
        clients.add(client);

        when(clientRepository.getAll(connection)).thenReturn(clients);

        clientService.updateClientStatus();

        assertEquals(EntityState.INACTIVE, client.getState());
        verify(clientRepository, never()).updateStatus(eq(connection), eq(client));
    }

    @Test
    public void testUpdateClientStatus_ClientAlreadyActiveWithActiveOrders() {
        Address address = new Address(1, "Main St", "1111-111", "Springfield", "USA");
        Client client = new Client(1, address, "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);
        client.setState(EntityState.ACTIVE);
        Order activeOrder = new Order(1, address, new Date(System.currentTimeMillis() - 10000), new Date(System.currentTimeMillis() + 10000), 100.0, ProcessState.PENDING);
        client.getOrders().add(activeOrder);

        List<Client> clients = new ArrayList<>();
        clients.add(client);

        when(clientRepository.getAll(connection)).thenReturn(clients);

        clientService.updateClientStatus();

        assertEquals(EntityState.ACTIVE, client.getState());
        verify(clientRepository, never()).updateStatus(eq(connection), eq(client));
    }

    @Test
    public void testUpdateClientStatus_ClientAlreadyInactiveWithNoActiveOrders() {
        Address address = new Address(1, "Main St", "1111-111", "Springfield", "USA");
        Client client = new Client(1, address, "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);
        client.setState(EntityState.INACTIVE);
        Order inactiveOrder = new Order(1, address, new Date(System.currentTimeMillis() - 20000), new Date(System.currentTimeMillis() - 10000), 100.0, ProcessState.PENDING);
        client.getOrders().add(inactiveOrder);

        List<Client> clients = new ArrayList<>();
        clients.add(client);

        when(clientRepository.getAll(connection)).thenReturn(clients);

        clientService.updateClientStatus();

        assertEquals(EntityState.INACTIVE, client.getState());
        verify(clientRepository, never()).updateStatus(eq(connection), eq(client));
    }

}
