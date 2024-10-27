package com.example.enhance_fitness_task.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderResponse {
    private Order order;
    private OrderMetaData orderMetaData;
    private boolean newInstance;

}
