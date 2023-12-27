package io.products.CashFlowGo.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mongodb.internal.connection.Time;
import io.products.CashFlowGo.model.Customer;
import io.products.CashFlowGo.model.request.enums.Currency;
import io.products.CashFlowGo.model.request.enums.TransactionType;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Document(collection = "Records")
public class Record {
    @Id
    @Hidden
    private String id;
    @Transient
    private Float price;
    @Hidden
    private Float inrPrice;
    @Hidden
    private Float usdPrice;
    private String description;
    private Customer customer;
    @CreatedDate
    @Hidden
    private Time timestamp;
    private Currency currency;
    private TransactionType transactionType;
}
