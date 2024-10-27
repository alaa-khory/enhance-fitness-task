package com.example.enhance_fitness_task.event.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderMessage {
    private String id;
    private String orderId;
    private String preferences;
    private String orderNotes;
    private boolean specialFlags;
    private String action;
    private String status;
}
