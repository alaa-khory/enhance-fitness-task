package com.example.enhance_fitness_task.model.user;

import com.example.enhance_fitness_task.model.Stage;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user_metadata")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMetaData {
    @Id
    private String id;
    private String userId;
    private String username;
    private List<Stage> stagesList;
    private String preferences;
}