package com.example.enhance_fitness_task.controller;

import com.example.enhance_fitness_task.model.order.Order;
import com.example.enhance_fitness_task.model.order.OrderRequest;
import com.example.enhance_fitness_task.model.order.OrderResponse;
import com.example.enhance_fitness_task.model.user.User;
import com.example.enhance_fitness_task.model.user.UserMetaData;
import com.example.enhance_fitness_task.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public String checkEndPoint(){
        return "Up and running";
    }


    @PostMapping("/registerUser")
    public User registerUser(@RequestBody User user){
        return userService.registerUser(user);
    }

    @GetMapping("/{userId}/orders")
    public List<Order> getUserOrdersHistory(@PathVariable Long userId){
        log.info("Starting to get order history for user with ID: {}", userId);

        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()){
            List<Order> orders = userService.getOrdersHistory(user.get());
            log.info("Order history retrieved for user with ID: {}", userId);
            return orders;
        }else {
            log.info("No user found with ID: {}", userId);
            return new ArrayList<>();
        }
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<OrderResponse> createUserOrder(@PathVariable Long userId, @RequestBody OrderRequest orderRequest, @RequestHeader(value = "RequestId", required = false) String requestId){
        log.info("Received createUserOrder request for userId: {}", userId);

        // Generate a unique RequestId if not provided
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
            log.info("Generated new requestId: {}", requestId);
        }
        Optional<User> user = userService.getUserById(userId);
        Optional<UserMetaData> userMetaData = userService.getUserMetaDataByUserId(userId.toString());
        if (user.isPresent()) {
            OrderResponse order = userService.createUserOrder(user.get(), orderRequest.getOrder(), orderRequest.getOrderMetaData(), requestId);
            log.info("createUserOrder successful for userId: {}", userId);
            return ResponseEntity.ok(order);
        }else {
            log.warn("User not found for userId: {}", userId);
            return ResponseEntity.status(404).body(new OrderResponse());
        }
    }

}
