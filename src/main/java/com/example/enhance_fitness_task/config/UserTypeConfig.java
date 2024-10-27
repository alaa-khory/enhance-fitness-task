package com.example.enhance_fitness_task.config;


import com.example.enhance_fitness_task.model.user.UserType;
import com.example.enhance_fitness_task.service.user.AdminBehavior;
import com.example.enhance_fitness_task.service.user.CustomerBehavior;
import com.example.enhance_fitness_task.service.user.UserTypeBehavior;
import com.example.enhance_fitness_task.service.user.VendorBehavior;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class UserTypeConfig {


    @Bean
    public Map<UserType, UserTypeBehavior> userTypeBehaviors(
            AdminBehavior adminBehavior,
            CustomerBehavior customerBehavior,
            VendorBehavior vendorBehavior
    ) {

        Map<UserType, UserTypeBehavior> userTypeBehaviorMap = new HashMap<>();
        userTypeBehaviorMap.put(UserType.ADMIN, adminBehavior);
        userTypeBehaviorMap.put(UserType.CUSTOMER, customerBehavior);
        userTypeBehaviorMap.put(UserType.VENDOR, vendorBehavior);
        return userTypeBehaviorMap;


    }

}
