package io.products.CashFlowGo.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.products.CashFlowGo.model.Customer;
import io.products.CashFlowGo.model.request.enums.Currency;
import io.products.CashFlowGo.model.request.enums.TransactionType;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.mapping.Sharded;

import java.time.LocalDateTime;

/**
 * Represents a financial transaction record.
 * Each record contains details such as transaction id, price, description,
 * customer details, timestamp, currency type, and transaction type (credit or debit).
 */
@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Document(collection = "Records")
public class Record {

    @MongoId
    @Schema(hidden = true)
    private String id;

    @Transient
    @Schema(description = "The price of the transaction", required = true)
    private Float price;

    @Schema(hidden = true)
    private Float inrPrice;

    @Schema(hidden = true)
    private Float usdPrice;

    @Schema(description = "The description of the transaction")
    private String description;

    @Schema(description = "The customer details associated with the record.")
    private Customer customer;

    @Schema(hidden = true)
    private LocalDateTime timestamp;

    @Schema(description = "The currency type in which the transactions occurred.")
    private Currency currency;

    @Schema(description = "The state of transaction Credit or Debit")
    private TransactionType transactionType;

    @Version
    @Schema(hidden = true)
    private Long version;

    public Record(String id, Float inrPrice, Float usdPrice, String description, Customer customer, LocalDateTime timestamp, Currency currency, TransactionType transactionType){
        this.id = id;
        this.inrPrice = inrPrice;
        this.usdPrice = usdPrice;
        this.description = description;
        this.customer = customer;
        this.timestamp = timestamp;
        this.currency = currency;
        this.transactionType = transactionType;
    }
}
