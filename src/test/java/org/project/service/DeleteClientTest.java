package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ClientException;
import org.project.model.Client;
import org.project.model.CompanyType;
import org.project.repository.ClientRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeleteClientTest {

    private ClientService clientService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private ClientRepository clientRepository;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        clientService = new ClientService();

        injectMock(clientService, "connection", connection);
        injectMock(clientService, "clientRepository", clientRepository);
    }

    private void injectMock(Object target, String fieldName, Object mock) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, mock);
    }

    @Test
    public void testDeleteClient_Success() {
        int clientID = 1;
        Client client = new Client(clientID, null, "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);

        when(clientRepository.getById(connection, clientID)).thenReturn(client);

        Client deletedClient = clientService.deleteClient(clientID);

        assertNotNull(deletedClient);
        assertEquals(clientID, deletedClient.getId());

        verify(clientRepository, times(1)).delete(connection, client);
    }

    @Test
    public void testDeleteClient_ClientNotFound() {
        int clientID = 1;

        when(clientRepository.getById(connection, clientID)).thenReturn(null);

        assertThrows(ClientException.class, () -> {
            clientService.deleteClient(clientID);
        });
    }

}
