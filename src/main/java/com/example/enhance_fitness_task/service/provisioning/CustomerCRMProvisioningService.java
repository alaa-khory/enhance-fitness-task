package com.example.enhance_fitness_task.service.provisioning;

import com.example.enhance_fitness_task.model.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class CustomerCRMProvisioningService implements ThirdPartyProvisioningService{
    @Override
    public void provision(User user) {
        log.info("Provisioning customer {} on CRM system", user.getEmail());
    }
}
