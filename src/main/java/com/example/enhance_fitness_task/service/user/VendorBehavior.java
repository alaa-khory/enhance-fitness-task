package com.example.enhance_fitness_task.service.user;

import com.example.enhance_fitness_task.model.user.User;
import com.example.enhance_fitness_task.service.provisioning.VendorServiceProvisioningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VendorBehavior  implements UserTypeBehavior{

    private final VendorServiceProvisioningService vendorServiceProvisioningService;

    public VendorBehavior(VendorServiceProvisioningService vendorServiceProvisioningService) {
        this.vendorServiceProvisioningService = vendorServiceProvisioningService;
    }

    @Override
    public void sendWelcomeEmail(User user) {
        log.info("Sending welcome email to Vendor: {} ", user.getEmail());

    }

    @Override
    public void provisionUser(User user) {
        vendorServiceProvisioningService.provision(user);
    }
}
