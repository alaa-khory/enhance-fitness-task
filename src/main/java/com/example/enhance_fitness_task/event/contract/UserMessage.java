package com.example.enhance_fitness_task.event.contract;

import com.example.enhance_fitness_task.model.Stage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserMessage implements Serializable {
    private String id;
    private Long userId;
    private List<Stage> stagesList;
    private String userName;
    private String action;
}