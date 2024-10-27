package com.example.enhance_fitness_task.service.user;

import com.example.enhance_fitness_task.model.user.User;
import com.example.enhance_fitness_task.service.provisioning.AdminProvisioningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminBehavior implements UserTypeBehavior {

    private final AdminProvisioningService adminProvisioningService;

    public AdminBehavior(AdminProvisioningService adminProvisioningService) {
        this.adminProvisioningService = adminProvisioningService;
    }


    @Override
    public void sendWelcomeEmail(User user) {
        log.info("Sending welcome email to Admin: {}", user.getEmail());
    }

    @Override
    public void provisionUser(User user) {
        adminProvisioningService.provision(user);
    }
}
