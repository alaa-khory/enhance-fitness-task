package com.example.enhance_fitness_task.repository;

import com.example.enhance_fitness_task.model.order.OrderMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMetaRepository extends MongoRepository<OrderMetaData, String> {
}
