# US009 - Register a Product

## 4. Tests 

**Test 1:** Check if the order is being registered correctly, being stored in the repository.

    @Test
    public void testRegisterOrder_Success() throws ParseException {

        int clientID = 1;
        int orderID = 1;
        String deliveryStreet = "Main St";
        String deliveryZipCode = "1111-111";
        String deliveryTown = "Springfield";
        String deliveryCountry = "USA";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date orderDate = sdf.parse("26/01/2024");
        Date deliveryDate = sdf.parse("26/01/2026");
        int price = 77;
        Map<String, Integer> productIDQuantity = new HashMap<>();
        productIDQuantity.put("prod1", 2);
        productIDQuantity.put("prod2", 1);

        Client client = new Client(clientID, null, "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);

        Product product1 = new Product("prod1", "mesa", "mesa madeira", new ProductCategory(1, "moveis"), 3, 2, "castanho", 12);
        when(productRepository.getProductByID(connection, "prod1")).thenReturn(product1);
        Product product2 = new Product("prod2", "cadeira", "cadeira madeira", new ProductCategory(1, "moveis"), 5, 3, "castanho", 9);
        when(productRepository.getProductByID(connection, "prod2")).thenReturn(product2);

        when(orderRepository.getOrderExists(connection, orderID)).thenReturn(false);
        when(addressRepository.findAddress(connection, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry)).thenReturn(null);
        when(addressRepository.getAddressCount(connection)).thenReturn(1);

        Order order = orderController.registerOrder(client, orderID, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry, orderDate, deliveryDate, price, productIDQuantity);

        assertNotNull(order);
        assertEquals(orderID, order.getId());

        verify(orderRepository, times(1)).save(eq(connection), any(Order.class), any(Client.class));
        verify(addressRepository, times(1)).save(eq(connection), any(Address.class));
    }
	

**Test 2:** Check that the order price is being recorded correctly - AC09.

    @Test
    public void testRegisterOrder_PriceCalculated() throws ParseException {

        int price1 = 12;
        int price2 = 9;
        Product product1 = new Product("prod1", "mesa", "mesa madeira", new ProductCategory(1, "moveis"), 3, 2, "castanho", price1);
        Product product2 = new Product("prod2", "cadeira", "cadeira madeira", new ProductCategory(1, "moveis"), 5, 3, "castanho", price2);
        Map<Product, Integer> productQuantity = new HashMap<>();
        productQuantity.put(product1, 2);
        productQuantity.put(product2, 1);

        int orderID = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date orderDate = sdf.parse("26/01/2024");
        Date deliveryDate = sdf.parse("26/01/2026");
        Client client = new Client(1, null, "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);

        Order order = orderService.registerOrder(client, orderID, "Main St", "1111-111", "Springfield", "USA", orderDate, deliveryDate, 0, productQuantity);

        int expectedPrice = (2 * 12) + (1 * 9);
        assertNotNull(order);
        assertEquals(expectedPrice, order.getPrice());

        verify(orderRepository, times(1)).save(eq(connection), any(Order.class), any(Client.class));
    }

**Test 3:** Check that it is not possible to register a order that already exists - AC03.

    @Test
    public void testRegisterOrder_OrderAlreadyExists() throws ParseException {

        int orderID = 1;
        when(orderRepository.getOrderExists(connection, orderID)).thenReturn(true);

        Client client = new Client(1, null, "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date orderDate = sdf.parse("26/01/2024");
        Date deliveryDate = sdf.parse("26/01/2026");
        Product product1 = new Product("prod1", "mesa", "mesa madeira", new ProductCategory(1, "moveis"), 3, 2, "castanho", 12);
        Product product2 = new Product("prod2", "cadeira", "cadeira madeira", new ProductCategory(1, "moveis"), 5, 3, "castanho", 9);
        Map<Product, Integer> productQuantity = new HashMap<>();
        productQuantity.put(product1, 2);
        productQuantity.put(product2, 1);

        assertThrows(OrderException.class, () -> {
            orderService.registerOrder(client, orderID, "Main St", "1111-111", "Springfield", "USA", orderDate, deliveryDate, 77, productQuantity);
        });
    }

**Test 4:** Check that it is not possible to register a order from a client not registered in the system - AC04.

    @Test
    public void testRegisterOrder_ClientNotFound() throws ParseException {

        int orderID = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date orderDate = sdf.parse("26/01/2024");
        Date deliveryDate = sdf.parse("26/01/2026");
        Map<Product, Integer> productQuantity = new HashMap<>();
        productQuantity.put(new Product("prod1", "mesa", "mesa madeira", new ProductCategory(1, "moveis"), 3, 2, "castanho", 12), 2);
        productQuantity.put(new Product("prod2", "cadeira", "cadeira madeira", new ProductCategory(1, "moveis"), 5, 3, "castanho", 9), 1);

        assertNull(orderService.registerOrder(null, orderID, "Main St", "1111-111", "Springfield", "USA", orderDate, deliveryDate, 77, productQuantity));

        verify(orderRepository, never()).save(eq(connection), any(Order.class), any(Client.class));
    }

**Test 5:** Check that it is not possible to register a order with a product not registered in the system - AC07.

    @Test
    public void testRegisterOrder_ProductNotFound() throws ParseException {

        int orderID = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date orderDate = sdf.parse("26/01/2024");
        Date deliveryDate = sdf.parse("26/01/2026");
        Map<String, Integer> productIDQuantity = new HashMap<>();
        productIDQuantity.put("prod1", 2);
        productIDQuantity.put("prod2", 1);

        Client client = new Client(1, null, "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);

        Product product1 = new Product("prod1", "mesa", "mesa madeira", new ProductCategory(1, "moveis"), 3, 2, "castanho", 12);
        when(productRepository.getProductByID(connection, "prod1")).thenReturn(product1);
        when(productRepository.getProductByID(connection, "prod2")).thenReturn(null);

        assertThrows(ProductException.class, () -> {
            orderController.registerOrder(client, orderID, "Main St", "1111-111", "Springfield", "USA", orderDate, deliveryDate, 77, productIDQuantity);
        });
    }

**Test 6:** Check that it is not possible to register a order with an invalid delivery date - AC05.

    @Test
    public void testRegisterOrder_InvalidDeliveryDate() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date orderDate = sdf.parse("26/01/2024");
        Date deliveryDate = sdf.parse("26/01/2022");

        int orderID = 1;
        Client client = new Client(1, null, "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);
        Map<Product, Integer> productQuantity = new HashMap<>();
        productQuantity.put(new Product("prod1", "mesa", "mesa madeira", new ProductCategory(1, "moveis"), 3, 2, "castanho", 12), 2);
        productQuantity.put(new Product("prod2", "cadeira", "cadeira madeira", new ProductCategory(1, "moveis"), 5, 3, "castanho", 9), 1);

        assertThrows(OrderException.class, () -> {
            orderService.registerOrder(client, orderID, "Main St", "1111-111", "Springfield", "USA", orderDate, deliveryDate, 77, productQuantity);
        });
    }

**Test 7:** Check that it is not possible to register a order with an invalid zip code - AC08.

    @Test
    public void testRegisterOrder_InvalidZipCode() throws ParseException {
        String invalidZipCode = "111";

        int orderID = 1;
        Client client = new Client(1, null, "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date orderDate = sdf.parse("26/01/2024");
        Date deliveryDate = sdf.parse("26/01/2022");
        Map<Product, Integer> productQuantity = new HashMap<>();
        productQuantity.put(new Product("prod1", "mesa", "mesa madeira", new ProductCategory(1, "moveis"), 3, 2, "castanho", 12), 2);
        productQuantity.put(new Product("prod2", "cadeira", "cadeira madeira", new ProductCategory(1, "moveis"), 5, 3, "castanho", 9), 1);

        assertThrows(OrderException.class, () -> {
            orderService.registerOrder(client, orderID, "Main St", invalidZipCode, "Springfield", "USA", orderDate, deliveryDate, 77, productQuantity);
        });
    }

**Test 8:** Check that the system reuses an existing address when registering a order with the same address details.

    @Test
    public void testRegisterOrder_ExistingAddress() throws ParseException {

        String deliveryStreet = "Main St";
        String deliveryZipCode = "1111-111";
        String deliveryTown = "Springfield";
        String deliveryCountry = "USA";
        Address existingAddress = new Address(5, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);

        int orderID = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date orderDate = sdf.parse("26/01/2024");
        Date deliveryDate = sdf.parse("26/01/2026");
        int price = 77;
        Map<Product, Integer> productQuantity = new HashMap<>();
        productQuantity.put(new Product("prod1", "mesa", "mesa madeira", new ProductCategory(1, "moveis"), 3, 2, "castanho", 12), 2);
        productQuantity.put(new Product("prod2", "cadeira", "cadeira madeira", new ProductCategory(1, "moveis"), 5, 3, "castanho", 9), 1);
        Client client = new Client(1, null, "John Doe", "123456789", 123456789, "john.doe@example.com", CompanyType.INDIVIDUAL);

        when(addressRepository.findAddress(connection, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry)).thenReturn(existingAddress);

        Order order = orderService.registerOrder(client, orderID, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry, orderDate, deliveryDate, price, productQuantity);

        assertNotNull(order);
        assertEquals(orderID, order.getId());
        assertEquals(existingAddress, order.getDeliveryAddress());

        verify(orderRepository, times(1)).save(eq(connection), any(Order.class), any(Client.class));
        verify(addressRepository, never()).save(eq(connection), any(Address.class));
    }

## 5. Construction (Implementation)

### Class OrderService 

```java
    public Order registerOrder(Client client, int orderID, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date orderDate, Date deliveryDate, int price, Map<Product, Integer> productQuantity) {
    if (deliveryDate.before(orderDate)) {
        throw OrderException.invalidDeliveryDate();
    }

    if (client == null) {
        return null;
    }

    if (orderRepository.getOrderExists(connection, orderID)) {
        throw OrderException.orderAlreadyExists(orderID);
    }

    if (!Validator.isValidZipCode(deliveryZipCode)) {
        throw OrderException.invalidZipCode();
    }

    Address address = addressRepository.findAddress(connection, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
    if (address == null) {
        int id = addressRepository.getAddressCount(connection);
        address = new Address(id, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
        addressRepository.save(connection, address);
    }

    if (price == 0) {
        price = calculatePrice(productQuantity);
    }

    Order order = new Order(orderID, address, orderDate, deliveryDate, price, productQuantity);

    orderRepository.save(connection, order, client);

    return order;
}
```
```java
    private int calculatePrice(Map<Product, Integer> productQuantity) {
    double result = 0;

    for (Map.Entry<Product, Integer> productEntry : productQuantity.entrySet()) {
        Product product = productEntry.getKey();
        double quantity = productEntry.getValue();
        result += product.getPrice() * quantity;
    }

    return (int) result;
}
```

## 6. Observations

n/a