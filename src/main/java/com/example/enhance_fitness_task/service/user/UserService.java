package com.example.enhance_fitness_task.service.user;

import com.example.enhance_fitness_task.event.contract.UserMessage;
import com.example.enhance_fitness_task.event.producer.UserEventProducer;
import com.example.enhance_fitness_task.model.*;
import com.example.enhance_fitness_task.model.order.Order;
import com.example.enhance_fitness_task.model.order.OrderMetaData;
import com.example.enhance_fitness_task.model.order.OrderResponse;
import com.example.enhance_fitness_task.model.user.User;
import com.example.enhance_fitness_task.model.user.UserMetaData;
import com.example.enhance_fitness_task.model.user.UserType;
import com.example.enhance_fitness_task.repository.OrderRepository;
import com.example.enhance_fitness_task.repository.UserMetaDataRepository;
import com.example.enhance_fitness_task.repository.UserRepository;
import com.example.enhance_fitness_task.service.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.example.enhance_fitness_task.config.AppConstants.*;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMetaDataRepository userMetaDataRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final Map<UserType, UserTypeBehavior> userTypeBehaviors;
    private final UserEventProducer userEventProducer;


    public UserService(UserRepository userRepository, UserMetaDataRepository userMetaDataRepository, OrderRepository orderRepository, OrderService orderService, Map<UserType, UserTypeBehavior> userTypeBehaviors, UserEventProducer userEventProducer) {
        this.userRepository = userRepository;
        this.userMetaDataRepository = userMetaDataRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.userTypeBehaviors = userTypeBehaviors;
        this.userEventProducer = userEventProducer;
    }

    @Transactional
    public User registerUser(User user) {
        log.info("Registering user with username: {}", user.getUsername());
        User savedUser = null;
        UserMetaData savedUserMetaData = null;
        try {
            savedUser = userRepository.save(user);
            log.info("User saved with ID: {}", savedUser.getId());
            UserMetaData userMetaData = new UserMetaData();
            userMetaData.setUserId(savedUser.getId().toString());
            userMetaData.setStagesList(List.of(new Stage(new Date(), USER_CREATE_IN_PROGRESS_STATUS)));
            userMetaData.setUsername(savedUser.getUsername());
            userMetaData.setPreferences("{}");
            savedUserMetaData = userMetaDataRepository.save(userMetaData);
            log.info("User metadata saved with ID: {}", savedUserMetaData.getId());


            UserMessage userMessage = new UserMessage();
            userMessage.setId(savedUserMetaData.getId());
            userMessage.setUserId(savedUser.getId());
            userMessage.setAction(USER_CREATE_ACTION);
            userMessage.setStagesList(userMetaData.getStagesList());
            userMessage.setUserName(savedUser.getUsername());
            userEventProducer.sendUserEvent(userMessage);
            log.info("User event sent for user ID: {}", savedUser.getId());


            UserTypeBehavior userTypeBehavior = userTypeBehaviors.get(savedUser.getUserType());
            userTypeBehavior.sendWelcomeEmail(savedUser);
            userTypeBehavior.provisionUser(savedUser);
        } catch (Exception ex) {
            log.error("Error occurred during user registration: {}", ex.getMessage(), ex);
            if (Objects.nonNull(savedUser)) {
                userRepository.delete(savedUser);
                log.info("Rolled back user creation for user ID: {}", savedUser.getId());
            }
            if (Objects.nonNull(userMetaDataRepository)) {
                userMetaDataRepository.delete(savedUserMetaData);
                log.info("Rolled back user metadata creation for ID: {}", savedUserMetaData.getId());
            }
            throw new RuntimeException("Failed to create user and rollback applied: " + ex.getMessage(), ex);

        }


        return savedUser;
    }

    public Optional<UserMetaData> getUserMetaDataByUserId(String userId) {
        log.info("Retrieving user metadata for user ID: {}", userId);
        Optional<UserMetaData> userMetaData = userMetaDataRepository.findByUserId(userId);
        return userMetaData.isPresent() ? userMetaData : Optional.empty();
    }

    public Optional<User> getUserById(Long userId) {
        log.info("Retrieving user for user ID: {}", userId);
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent() ? user : Optional.empty();
    }

    public OrderResponse createUserOrder(User user, Order order, OrderMetaData orderMetaData, String requestId) {
        log.info("Starting createUserOrder for userId: {}", user.getId());
        order.setUserId(user.getId());
        OrderResponse orderResponse = orderService.createOrder(order, orderMetaData, requestId);
        log.info("createUserOrder completed for userId: {}", user.getId());
        return orderResponse;
    }

    public List<Order> getOrdersHistory(User user) {
        log.info("Retrieving order history for user ID: {}", user.getId());
        return orderRepository.getOrdersByUserId(user.getId());
    }


}
