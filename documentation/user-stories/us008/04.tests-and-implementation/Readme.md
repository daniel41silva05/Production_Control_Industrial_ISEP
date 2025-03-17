# US008 - Consult Active Orders

## 4. Tests 

**Test 1:** Check if the client with the inactive status but containing active orders has its status changed - AC03.

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

**Test 2:** Check if the client with the activated status but without active orders has its status changed - AC03.

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

**Test 3:** Check if the client without orders has the inactive status.

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

**Test 4:** Check if the client with the active status and containing active orders has not had its status changed - AC03.

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

**Test 5:** Check if the customer with inactive status and does not contain active orders has not had its status changed - AC03.

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

## 5. Construction (Implementation)

### Class ClientService 

```java
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
```
```java
public List<Client> getClients() {
    return clientRepository.getAll(connection);
}
```

## 6. Observations

n/a