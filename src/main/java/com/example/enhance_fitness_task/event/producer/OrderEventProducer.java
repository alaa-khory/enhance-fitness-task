package com.example.enhance_fitness_task.event.producer;

import com.example.enhance_fitness_task.event.contract.OrderMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.enhance_fitness_task.config.AppConstants.ORDER_QUEUE_NAME;

@Service
public class OrderEventProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderEvent(OrderMessage orderMessage) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(new ObjectMapper().writeValueAsBytes(orderMessage))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build();
        rabbitTemplate.convertAndSend(ORDER_QUEUE_NAME, message);
    }
}
