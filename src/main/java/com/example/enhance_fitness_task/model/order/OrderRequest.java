package com.example.enhance_fitness_task.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Order order;
    private OrderMetaData orderMetaData;
}
