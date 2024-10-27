package com.example.enhance_fitness_task.order;

import com.example.enhance_fitness_task.event.producer.OrderEventProducer;
import com.example.enhance_fitness_task.model.order.Order;
import com.example.enhance_fitness_task.model.order.OrderMetaData;
import com.example.enhance_fitness_task.model.order.OrderResponse;
import com.example.enhance_fitness_task.repository.OrderMetaRepository;
import com.example.enhance_fitness_task.repository.OrderRepository;
import com.example.enhance_fitness_task.service.IdempotencyService;
import com.example.enhance_fitness_task.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.NoTransactionException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMetaRepository orderMetaRepository;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private OrderEventProducer orderEventProducer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository, orderMetaRepository, idempotencyService, orderEventProducer);
    }

    @Test
    public void testCreateOrder_Success() {
        // Arrange
        Order order = new Order(null, new Date(), "Item1, Item2", new BigDecimal("99.99"), 1L);

        OrderMetaData orderMetaData = new OrderMetaData("12345", "order-001", "extra spicy", "please deliver after 5 PM", true, "Processing");

        String requestId = "requestId";

        when(idempotencyService.isRequestProcessed(requestId)).thenReturn(false);
        when(orderRepository.save(order)).thenReturn(order);
        order.setOrderId(1L);
        when(orderMetaRepository.save(orderMetaData)).thenReturn(orderMetaData);

        // Act
        OrderResponse response = orderService.createOrder(order, orderMetaData, requestId);

        // Assert
        assertTrue(response.isNewInstance());
        verify(orderRepository).save(order);
        verify(orderMetaRepository).save(orderMetaData);
        verify(idempotencyService).markRequestProcessed(requestId);
    }

    @Test
    public void testCreateOrder_IdempotentRequest() {
        // Arrange
        Order order = new Order(null, new Date(), "Item1, Item2", new BigDecimal("99.99"), 1L);

        OrderMetaData orderMetaData = new OrderMetaData("12345", "order-001", "extra spicy", "please deliver after 5 PM", true, "Processing");
        String requestId = "requestId";

        when(idempotencyService.isRequestProcessed(requestId)).thenReturn(true);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        // Act
        OrderResponse response = orderService.createOrder(order, orderMetaData, requestId);

        // Assert
        assertFalse(response.isNewInstance());
        assertEquals(order, response.getOrder());
        verify(orderRepository, never()).save(any());
        verify(orderMetaRepository, never()).save(any());
        verify(idempotencyService, never()).markRequestProcessed(requestId);
    }

    @Test
    public void testCreateOrder_MongoSaveFailure() {
        // Arrange
        Order order = new Order(null, new Date(), "Item1, Item2", new BigDecimal("99.99"), 1L);

        OrderMetaData orderMetaData = new OrderMetaData("12345", "order-001", "extra spicy", "please deliver after 5 PM", true, "Processing");
        String requestId = "requestId";

        when(idempotencyService.isRequestProcessed(requestId)).thenReturn(false);
        when(orderRepository.save(order)).thenReturn(order);
        order.setOrderId(1L);
        doThrow(new RuntimeException("MongoDB error"))
                .when(orderMetaRepository).save(orderMetaData);

        // Act & Assert
        Exception exception = assertThrows(NoTransactionException.class, () -> {
            orderService.createOrder(order, orderMetaData, requestId);
        });
        assertEquals("No transaction aspect-managed TransactionStatus in scope", exception.getMessage());

        verify(orderRepository).save(order);
        verify(orderMetaRepository).save(orderMetaData);
        verify(idempotencyService, never()).markRequestProcessed(requestId);
    }

    @Test
    public void testCreateOrder_PostgresSaveFailure() {
        // Arrange
        Order order = new Order(null, new Date(), "Item1, Item2", new BigDecimal("99.99"), 1L);

        OrderMetaData orderMetaData = new OrderMetaData("12345", "order-001", "extra spicy", "please deliver after 5 PM", true, "Processing");
        String requestId = "requestId";

        when(idempotencyService.isRequestProcessed(requestId)).thenReturn(false);
        doThrow(new RuntimeException("Postgres error"))
                .when(orderRepository).save(order);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(order, orderMetaData, requestId);
        });
        assertEquals("No transaction aspect-managed TransactionStatus in scope", exception.getMessage());

        verify(orderRepository).save(order);
        verify(orderMetaRepository, never()).save(any());
        verify(idempotencyService, never()).markRequestProcessed(requestId);
    }
}
