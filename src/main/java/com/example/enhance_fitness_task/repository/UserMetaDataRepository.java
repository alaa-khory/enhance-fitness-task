package com.example.enhance_fitness_task.repository;

import com.example.enhance_fitness_task.model.user.UserMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMetaDataRepository extends MongoRepository<UserMetaData, String> {
    Optional<UserMetaData> findByUserId(String userId);
}
