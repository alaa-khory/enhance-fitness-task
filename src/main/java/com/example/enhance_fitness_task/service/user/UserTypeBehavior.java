package com.example.enhance_fitness_task.service.user;

import com.example.enhance_fitness_task.model.user.User;

public interface UserTypeBehavior {

    void sendWelcomeEmail(User user);
    void provisionUser(User user);

}
