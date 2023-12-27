package io.products.CashFlowGo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a customer with details such as name, phone number, and email.
 */
@Data
@AllArgsConstructor
public class Customer {
    @Schema(description = "The name of the customer", example = "Suresh")
    private String name;
    @Schema(description = "The phone number of the customer", example = "9876543422")
    private String phoneNumber;
    @Schema(description = "The email number of the customer", example = "suresh@gmail.com")
    private String email;
}
