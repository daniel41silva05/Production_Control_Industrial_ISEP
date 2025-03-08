# US004 - Consult Client Status

## 4. Tests 

**Test 1:** Check if the client is being updated correctly, being stored in the repository.

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
	

**Test 2:** Check that it's not possible to update a client that doesn't exist - AC01.

    @Test
    public void testDeleteClient_ClientNotFound() {
        assertNull(clientService.updateClient(null, "John Doe", "123456789", "Main St", "9999-999", "Springfield", "USA", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL));
    }

**Test 3:** Check that it is not possible to update a client with an invalid phone number - AC04.

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

**Test 4:** Check that it is not possible to update a client with an invalid email format - AC05.

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

**Test 5:** Check that it is not possible to update a client with an invalid zip code - AC06.

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

**Test 6:** Check that the system reuses an existing address when registering a client with the same address details.

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

## 5. Construction (Implementation)

### Class ClientService 

```java
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