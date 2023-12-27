package io.products.CashFlowGo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class Customer {
    private String name;
    private Integer phoneNumber;
    private Integer email;
}
