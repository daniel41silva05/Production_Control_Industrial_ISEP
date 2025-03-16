# US006 - Cancel an Order

## 4. Tests

**Test 1:** Check if the order is being deleted correctly and removed from the repository - AC02.

    @Test
    public void testDeleteClient_Success() throws ParseException {
        int orderID = 1;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date orderDate = sdf.parse("26/01/2024");
        Date deliveryDate = sdf.parse("26/01/2026");
        Order order = new Order(orderID, new Address(2, "Main St", "1111-111", "Springfield", "USA"), orderDate, deliveryDate, 10, new HashMap<>());

        when(orderRepository.getByID(connection, orderID)).thenReturn(order);

        Order deletedOrder = orderService.deleteOrder(orderID);

        assertNotNull(deletedOrder);
        assertEquals(orderID, deletedOrder.getId());

        verify(orderRepository, times(1)).delete(connection, order);
    }

**Test 2:** Check that it's not possible to delete a order that doesn't exist - AC01.

    @Test
    public void testDeleteOrder_OrderNotFound() {
        int orderID = 1;

        when(orderRepository.getByID(connection, orderID)).thenReturn(null);

        assertThrows(OrderException.class, () -> {
            orderService.deleteOrder(orderID);
        });
    }

## 5. Construction (Implementation)

### Class OrderService

```java
public Order deleteOrder (int id) {
    Order order = getOrderByID(id);

    orderRepository.delete(connection, order);

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