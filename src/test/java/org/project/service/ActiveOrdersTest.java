package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.model.Order;
import org.project.repository.OrderRepository;
import org.project.data.DatabaseConnection;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ActiveOrdersTest {

    private OrderService orderService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(connection, orderRepository, null);
    }

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

    @Test
    public void testActiveOrders_ReturnsEmptyListWhenNoOrders() {
        when(orderRepository.getAll(connection)).thenReturn(Collections.emptyList());

        List<Order> activeOrders = orderService.activeOrders();

        assertTrue(activeOrders.isEmpty());
    }

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

}
