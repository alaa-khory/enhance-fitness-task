package com.example.enhance_fitness_task.service.order;

import com.example.enhance_fitness_task.event.contract.OrderMessage;
import com.example.enhance_fitness_task.event.producer.OrderEventProducer;
import com.example.enhance_fitness_task.model.order.Order;
import com.example.enhance_fitness_task.model.order.OrderMetaData;
import com.example.enhance_fitness_task.model.order.OrderResponse;
import com.example.enhance_fitness_task.repository.OrderMetaRepository;
import com.example.enhance_fitness_task.repository.OrderRepository;
import com.example.enhance_fitness_task.service.IdempotencyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMetaRepository orderMetaRepository;
    private final IdempotencyService idempotencyService;
    private final OrderEventProducer orderEventProducer;

    public OrderService(OrderRepository orderRepository, OrderMetaRepository orderMetaRepository, IdempotencyService idempotencyService, OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.orderMetaRepository = orderMetaRepository;
        this.idempotencyService = idempotencyService;
        this.orderEventProducer = orderEventProducer;
    }


    @Transactional
    public OrderResponse createOrder(Order order, OrderMetaData orderMetaData, String requestId) {
        log.info("Starting createOrder with requestId: {}", requestId);

        OrderResponse orderResponse = new OrderResponse();

        try {
            // Idempotency Check
            if (idempotencyService.isRequestProcessed(requestId)) {
                log.info("RequestId {} already processed. Returning existing order.", requestId);
                orderResponse.setOrder(handleGetOrderId(order));
                orderResponse.setNewInstance(false); // Indicator that an existing order is returned
                return orderResponse;
            }
            log.info("Saving new order with requestId: {}", requestId);
            Order savedOrder = orderRepository.save(order);
            handleSetOrderId(orderMetaData, savedOrder);
            orderMetaRepository.save(orderMetaData);

            idempotencyService.markRequestProcessed(requestId); // Mark request as processed

            log.info("Order created successfully with requestId: {}", requestId);
            orderResponse.setOrder(order);
            orderResponse.setOrderMetaData(orderMetaData);
            orderResponse.setNewInstance(true); // Indicator that a new order is created

            // Prepare OrderMessage
            OrderMessage orderMessage = createOrderMessage(orderMetaData, safeGetOrderId(order));

            // Publish to RabbitMQ
            sendOrderEvent(orderMessage);

            log.info("Order created successfully with requestId: {}", requestId);

        } catch (DataAccessException e) {
            log.error("Database exception during createOrder: {}", e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // Manual rollback
            // Compensate MongoDB insertion by removing the document if PostgreSQL fails
            compensateMongoRollback(orderMetaData.getId());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during createOrder : {}", e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // Manual rollback
            // Compensate MongoDB insertion by removing the document if PostgreSQL fails
            compensateMongoRollback(orderMetaData.getId());
            throw e; // Rethrow to maintain transactional rollback
        }
        return orderResponse;
    }

    private Order handleGetOrderId(Order order) {
        try {
            return orderRepository.findById(order.getOrderId()).orElse(order);
        } catch (Exception e) {
            log.error("Error retrieving order by ID: {}", e.getMessage(), e);
            return order; // In case of error, return the passed order
        }
    }

    private void handleSetOrderId(OrderMetaData orderMetaData, Order savedOrder) {
        try {
            orderMetaData.setOrderId(savedOrder.getOrderId().toString());
        } catch (Exception e) {
            log.error("Error setting order ID in metadata: {}", e.getMessage(), e);
            orderMetaData.setOrderId("0");
        }
    }

    private String safeGetOrderId(Order order) {
        try {
            return order.getOrderId().toString();
        } catch (Exception e) {
            log.error("Error retrieving order ID: {}", e.getMessage(), e);
            return "0"; // Default value in case of error
        }
    }

    private OrderMessage createOrderMessage(OrderMetaData orderMetaData, String orderId) {
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setId(orderMetaData.getId());
        orderMessage.setOrderId(orderId);
        orderMessage.setPreferences(orderMetaData.getPreferences());
        orderMessage.setOrderNotes(orderMetaData.getOrderNotes());
        orderMessage.setSpecialFlags(orderMetaData.isSpecialFlags());
        orderMessage.setAction("CREATE");
        return orderMessage;
    }

    // Retry mechanism can be implemented here
    public void sendOrderEvent(OrderMessage orderMessage) {
        try {
            orderEventProducer.sendOrderEvent(orderMessage);
        } catch (JsonProcessingException e) {
            log.error("Error serializing OrderMessage: {}", e.getMessage(), e);
        }
    }

    private void compensateMongoRollback(String metaDataId) {
        try {
            orderMetaRepository.deleteById(metaDataId);
            log.info("Successfully rolled back MongoDB document with id: {}", metaDataId);
        } catch (Exception e) {
            log.error("Failed to roll back MongoDB document with id {}: {}", metaDataId, e.getMessage(), e);
        }
    }
}
