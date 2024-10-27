package com.example.enhance_fitness_task.event.consumer;

import com.example.enhance_fitness_task.event.contract.UserMessage;
import com.example.enhance_fitness_task.model.Stage;
import com.example.enhance_fitness_task.model.user.UserMetaData;
import com.example.enhance_fitness_task.repository.UserMetaDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static com.example.enhance_fitness_task.config.AppConstants.*;

@Service
@Slf4j
public class UserEventListener {

    private final UserMetaDataRepository userMetaDataRepository;

    public UserEventListener(UserMetaDataRepository userMetaDataRepository) {
        this.userMetaDataRepository = userMetaDataRepository;
    }

    @RabbitListener(queues = USER_QUEUE_NAME)
    public void handleUserEvent(UserMessage userMessage) {
        log.info("UserEventListener received the message and trying to deserialize it queue: {}, message: {}", USER_QUEUE_NAME, userMessage);
        switch (userMessage.getAction()) {
            case USER_CREATE_ACTION:
                Optional<UserMetaData> userMetaDataOptional = userMetaDataRepository.findById(userMessage.getId());
                UserMetaData userMetaData;
                if (userMetaDataOptional.isPresent()) {
                    userMetaData = userMetaDataOptional.get();
                    userMetaData.getStagesList().add(new Stage(new Date(), USER_CREATED_STATUS));
                    userMetaDataRepository.save(userMetaData);
                    log.info("Update userMetaData successfully for this user {}", userMessage.getUserName());
                } else {
                    log.info("No UserMeta data for this user {}", userMessage);
                }
                break;
            default:
                log.warn("Unhandled action type for user message: {}", userMessage.getAction());
                break;
        }

    }

}
