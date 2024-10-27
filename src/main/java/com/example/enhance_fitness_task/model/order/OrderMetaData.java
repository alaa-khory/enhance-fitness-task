package com.example.enhance_fitness_task.model.order;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "order_metadata")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderMetaData {

    @Id
    private String id;

    private String orderId;

    private String preferences;
    private String orderNotes;
    private boolean specialFlags;
    private String status;

}
