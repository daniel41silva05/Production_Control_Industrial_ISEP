# US007 - Update Order Data

## 4. Tests 

**Test 1:** Check if the order is being updated correctly, being stored in the repository.

    @Test
    public void testUpdateClient_Success() throws ParseException {

        int orderID = 1;

        String newDeliveryStreet = "Main St";
        String newDeliveryZipCode = "1111-111";
        String newDeliveryTown = "Springfield";
        String newDeliveryCountry = "USA";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date newOrderDate = sdf.parse("26/01/2024");
        Date newDeliveryDate = sdf.parse("26/01/2026");
        int newPrice = 77;

        Order existingOrder = new Order(orderID, new Address(1, "Rua Clérigos", "1222-222", "Porto", "portugal"), sdf.parse("26/01/2023"), sdf.parse("01/01/2024"), 70, new HashMap<>());

        when(orderRepository.getByID(connection, orderID)).thenReturn(existingOrder);
        when(addressRepository.findAddress(connection, newDeliveryStreet, newDeliveryZipCode, newDeliveryTown, newDeliveryCountry)).thenReturn(null);
        when(addressRepository.getAddressCount(connection)).thenReturn(2);

        Order updatedOrder = orderService.updateOrder(existingOrder, newDeliveryStreet, newDeliveryZipCode, newDeliveryTown, newDeliveryCountry, newOrderDate, newDeliveryDate, newPrice);

        assertNotNull(updatedOrder);
        assertEquals(newDeliveryStreet, updatedOrder.getDeliveryAddress().getStreet());
        assertEquals(newDeliveryZipCode, updatedOrder.getDeliveryAddress().getZipCode());
        assertEquals(newDeliveryTown, updatedOrder.getDeliveryAddress().getTown());
        assertEquals(newDeliveryCountry, updatedOrder.getDeliveryAddress().getCountry());
        assertEquals(newOrderDate, updatedOrder.getOrderDate());
        assertEquals(newDeliveryDate, updatedOrder.getDeliveryDate());
        assertEquals(newPrice, updatedOrder.getPrice());

        verify(orderRepository, times(1)).update(eq(connection), any(Order.class));
        verify(addressRepository, times(1)).save(eq(connection), any(Address.class));
    }

**Test 2:** Check that it's not possible to update an order that doesn't exist - AC01.

    @Test
    public void testUpdateOrder_OrderNotFound() {
        assertNull(orderService.updateOrder(null, "Rua Clérigos", "1222-222", "Porto", "portugal", new Date(), new Date(), 77));
    }

**Test 3:** Check that it is not possible to update an order with an invalid delivery date - AC04.

    @Test
    public void testUpdateOrder_InvalidDeliveryDate() throws ParseException {
        int orderID = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date orderDate = sdf.parse("26/01/2024");
        Date deliveryDate = sdf.parse("26/01/2020");

        Order existingOrder = new Order(orderID, new Address(1, "Old St", "9999-999", "Old Town", "Old Country"), new Date(), new Date(), 100, new HashMap<>());

        when(orderRepository.getByID(connection, orderID)).thenReturn(existingOrder);

        assertThrows(OrderException.class, () -> {
            orderService.updateOrder(existingOrder, "Main St", "1111-111", "Springfield", "USA", orderDate, deliveryDate, 120);
        });
    }

**Test 4:** Check that it is not possible to update an order with an invalid zip code - AC05.

    @Test
    public void testUpdateOrder_InvalidZipCode() {
        int orderID = 1;
        String invalidZipCode = "111";

        Order existingOrder = new Order(orderID, new Address(1, "Old St", "9999-999", "Old Town", "Old Country"), new Date(), new Date(), 100, new HashMap<>());

        when(orderRepository.getByID(connection, orderID)).thenReturn(existingOrder);

        assertThrows(OrderException.class, () -> {
            orderService.updateOrder(existingOrder, "Main St", invalidZipCode, "Springfield", "USA", new Date(), new Date(), 120);
        });
    }

**Test 5:** Check that the system reuses an existing address when registering an order with the same address details.

    @Test
    public void testUpdateOrder_ExistingAddress() {
        int orderID = 1;
        String street = "Main St";
        String zipCode = "1111-111";
        String town = "Springfield";
        String country = "USA";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date orderDate = new Date();
        Date deliveryDate = new Date();
        int price = 150;

        Order existingOrder = new Order(orderID, new Address(1, "Old St", "9999-999", "Old Town", "Old Country"), orderDate, deliveryDate, 100, new HashMap<>());
        Address newAddress = new Address(2, street, zipCode, town, country);

        when(orderRepository.getByID(connection, orderID)).thenReturn(existingOrder);
        when(addressRepository.findAddress(connection, street, zipCode, town, country)).thenReturn(newAddress);

        Order updatedOrder = orderService.updateOrder(existingOrder, street, zipCode, town, country, orderDate, deliveryDate, price);

        assertNotNull(updatedOrder);
        assertEquals(newAddress, updatedOrder.getDeliveryAddress());

        verify(addressRepository, never()).save(eq(connection), any(Address.class));
    }

## 5. Construction (Implementation)

### Class ClientService 

```java
public Order updateOrder (Order order, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date orderDate, Date deliveryDate, double price) {
    if (deliveryDate.before(orderDate)) {
        throw OrderException.invalidDeliveryDate();
    }

    if (order == null) {
        return null;
    }

    Address address = order.getDeliveryAddress();
    if (!address.getStreet().equals(deliveryStreet) || !address.getZipCode().equals(deliveryZipCode) || !address.getTown().equals(deliveryTown) || !address.getCountry().equals(deliveryCountry)) {
        if (!Validator.isValidZipCode(deliveryZipCode)) {
            throw OrderException.invalidZipCode();
        }

        address = addressRepository.findAddress(connection, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
        if (address == null) {
            int id = addressRepository.getAddressCount(connection);
            address = new Address(id, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
            addressRepository.save(connection, address);
        }
    }

    order.setDeliveryAddress(address);
    order.setOrderDate(orderDate);
    order.setDeliveryDate(deliveryDate);
    order.setPrice(price);

    orderRepository.update(connection, order);

    return order;
}
```
```java
    public Order getOrderByID (int orderID) {
    Order order = orderRepository.getByID(connection, orderID);

    if (order == null) {
        throw OrderException.orderNotFound(orderID);
    }

    return order;
}
```

## 6. Observations

n/a