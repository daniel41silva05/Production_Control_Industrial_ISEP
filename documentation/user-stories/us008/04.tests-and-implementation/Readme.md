# US008 - Consult Active Orders

## 4. Tests 

**Test 1:** Check if only include orders with a delivery date after the current one - AC02.

    @Test
    public void testActiveOrders_ReturnsOnlyFutureOrders() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);
        Date pastDate = new Date(System.currentTimeMillis() - 86400000);

        Order order1 = mock(Order.class);
        Order order2 = mock(Order.class);

        when(order1.getDeliveryDate()).thenReturn(futureDate);
        when(order2.getDeliveryDate()).thenReturn(pastDate);

        when(orderRepository.getAll(connection)).thenReturn(Arrays.asList(order1, order2));

        List<Order> activeOrders = orderService.activeOrders();

        assertEquals(1, activeOrders.size());
        assertTrue(activeOrders.contains(order1));
        assertFalse(activeOrders.contains(order2));
    }

**Test 2:** Check if there are no orders returns empty.

    @Test
    public void testActiveOrders_ReturnsEmptyListWhenNoOrders() {
        when(orderRepository.getAll(connection)).thenReturn(Collections.emptyList());

        List<Order> activeOrders = orderService.activeOrders();

        assertTrue(activeOrders.isEmpty());
    }

**Test 3:** Check if there are only inactive orders returns empty.

    @Test
    public void testActiveOrders_ReturnsEmptyListWhenAllOrdersArePast() {
        Date pastDate1 = new Date(System.currentTimeMillis() - 86400000);
        Date pastDate2 = new Date(System.currentTimeMillis() - 172800000);

        Order order1 = mock(Order.class);
        Order order2 = mock(Order.class);

        when(order1.getDeliveryDate()).thenReturn(pastDate1);
        when(order2.getDeliveryDate()).thenReturn(pastDate2);

        when(orderRepository.getAll(connection)).thenReturn(Arrays.asList(order1, order2));

        List<Order> activeOrders = orderService.activeOrders();

        assertTrue(activeOrders.isEmpty());
    }

## 5. Construction (Implementation)

### Class OrderService 

```java
public List<Order> activeOrders () {
    List<Order> activeOrders = new ArrayList<>();

    List<Order> orders = orderRepository.getAll(connection);

    for (Order order : orders) {
        if (order.getDeliveryDate().after(new Date())) {
            activeOrders.add(order);
        }
    }

    return activeOrders;
}
```

## 6. Observations

n/a