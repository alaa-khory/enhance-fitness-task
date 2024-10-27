package com.example.enhance_fitness_task.event.consumer;

import com.example.enhance_fitness_task.event.contract.OrderMessage;
import com.example.enhance_fitness_task.model.order.OrderMetaData;
import com.example.enhance_fitness_task.repository.OrderMetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.enhance_fitness_task.config.AppConstants.ORDER_QUEUE_NAME;

@Service
@Slf4j
public class OrderEventListener {

    private final OrderMetaRepository orderMetaDataRepository;
    private static final String ORDER_CREATE_ACTION = "CREATE";
    private static final String ORDER_CREATED_STATUS = "Order Created";

    public OrderEventListener(OrderMetaRepository orderMetaDataRepository) {
        this.orderMetaDataRepository = orderMetaDataRepository;
    }

    @RabbitListener(queues = ORDER_QUEUE_NAME)
    public void handleOrderEvent(OrderMessage orderMessage) {
        log.info("OrderEventListener received the message and trying to deserialize it queue: {}, message: {}", ORDER_QUEUE_NAME, orderMessage);

        switch (orderMessage.getAction()) {
            case ORDER_CREATE_ACTION:
                Optional<OrderMetaData> orderMetaDataOptional = orderMetaDataRepository.findById(orderMessage.getId());
                OrderMetaData orderMetaData;
                if (orderMetaDataOptional.isPresent()) {
                    orderMetaData = orderMetaDataOptional.get();
                    orderMetaData.setOrderNotes(orderMessage.getOrderNotes());
                    orderMetaData.setStatus("POSTED");
                    orderMetaDataRepository.save(orderMetaData);
                    log.info("Updated orderMetaData successfully for order ID {}", orderMessage.getOrderId());
                } else {
                    log.info("No OrderMeta data found for order {}", orderMessage);
                }
                break;

            default:
                log.warn("Unhandled action type for order message: {}", orderMessage.getAction());
                break;
        }
    }
}
