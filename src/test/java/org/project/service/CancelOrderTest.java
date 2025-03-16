package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ClientException;
import org.project.exceptions.OrderException;
import org.project.model.Address;
import org.project.model.Order;
import org.project.repository.AddressRepository;
import org.project.repository.OrderRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CancelOrderTest {

    private OrderService orderService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AddressRepository addressRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        orderService = new OrderService(connection, orderRepository, addressRepository);
    }

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

    @Test
    public void testDeleteOrder_OrderNotFound() {
        int orderID = 1;

        when(orderRepository.getByID(connection, orderID)).thenReturn(null);

        assertThrows(OrderException.class, () -> {
            orderService.deleteOrder(orderID);
        });
    }

}
