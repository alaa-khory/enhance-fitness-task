package com.example.enhance_fitness_task.service.user;

import com.example.enhance_fitness_task.model.user.User;
import com.example.enhance_fitness_task.service.provisioning.CustomerCRMProvisioningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerBehavior implements UserTypeBehavior{

    private final CustomerCRMProvisioningService customerCRMProvisioningService;

    public CustomerBehavior(CustomerCRMProvisioningService customerCRMProvisioningService) {
        this.customerCRMProvisioningService = customerCRMProvisioningService;
    }

    @Override
    public void sendWelcomeEmail(User user) {
        log.info("Sending welcome email to Customer: {}", user.getEmail());
    }

    @Override
    public void provisionUser(User user) {
        customerCRMProvisioningService.provision(user);
    }
}
