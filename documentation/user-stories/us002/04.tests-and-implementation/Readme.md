# US002 - Delete a Client

## 4. Tests 

**Test 1:** Check if the client is being registered correctly, being stored in the repository.

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
	

**Test 2:** Check that it is not possible to register a client that already exists - AC03.

    @Test
    public void testRegisterClient_ClientAlreadyExists() {

        int clientID = 1;
        when(clientRepository.getClientExists(connection, clientID)).thenReturn(true);

        assertThrows(ClientException.class, () -> {
            clientService.registerClient(clientID, "John Doe", "123456789", "Main St", "1111-111", "Springfield", "USA", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);
        });
    }

**Test 3:** Check that it is not possible to register a client with an invalid phone number - AC04.

    @Test
    public void testRegisterClient_InvalidPhoneNumber() {

        int clientID = 1;
        int invalidPhoneNumber = 123;

        when(clientRepository.getClientExists(connection, clientID)).thenReturn(false);

        assertThrows(ClientException.class, () -> {
            clientService.registerClient(clientID, "John Doe", "123456789", "Main St", "1111-111", "Springfield", "USA", invalidPhoneNumber, "john.doe@example.com", CompanyType.INDIVIDUAL);
        });
    }

**Test 4:** Check that it is not possible to register a client with an invalid email format - AC05.

    @Test
    public void testRegisterClient_InvalidEmail() {
        int clientID = 1;
        String invalidEmail = "invalid-email";

        when(clientRepository.getClientExists(connection, clientID)).thenReturn(false);

        assertThrows(ClientException.class, () -> {
            clientService.registerClient(clientID, "John Doe", "123456789", "Main St", "1111-111", "Springfield", "USA", 123456789, invalidEmail, CompanyType.INDIVIDUAL);
        });
    }

**Test 5:** Check that it is not possible to register a client with an invalid zip code - AC06.

    @Test
    public void testRegisterClient_InvalidZipCode() {
        int clientID = 1;
        String invalidZipCode = "111";

        when(clientRepository.getClientExists(connection, clientID)).thenReturn(false);

        assertThrows(ClientException.class, () -> {
            clientService.registerClient(clientID, "John Doe", "123456789", "Main St", invalidZipCode, "Springfield", "USA", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);
        });
    }

**Test 6:** Check that the system reuses an existing address when registering a client with the same address details.

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

## 5. Construction (Implementation)

### Class ClientService 

```java
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
```

## 6. Observations

n/a