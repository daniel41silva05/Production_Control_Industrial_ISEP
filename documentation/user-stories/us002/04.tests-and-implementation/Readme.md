# US002 - Delete a Client

## 4. Tests 

**Test 1:** Check if the client is being deleted correctly and removed from the repository - AC02.

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
	

**Test 2:** Check that it's not possible to delete a client that doesn't exist - AC01.

    @Test
    public void testDeleteClient_ClientNotFound() {
        int clientID = 1;

        when(clientRepository.getById(connection, clientID)).thenReturn(null);

        assertThrows(ClientException.class, () -> {
            clientService.deleteClient(clientID);
        });
    }

## 5. Construction (Implementation)

### Class ClientService 

```java
public Client deleteClient (int id) {
    Client client = getClientByID(id);

    clientRepository.delete(connection, client);

    return client;
}
```
```java
public Client getClientByID(int id) {
    Client client = clientRepository.getById(connection, id);

    if (client == null) {
        throw ClientException.clientNotFound(id);
    }

    return client;
}
```

## 6. Observations

n/a