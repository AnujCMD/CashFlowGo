package io.products.CashFlowGo.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.products.CashFlowGo.model.request.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Response object containing details about a transaction operation.
 * It includes a list of transaction records, HTTP status, and a message describing the status of the request.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RecordResponse {
    @Schema(description = "The list of transaction records")
    private List<Record> recordList;
    @Schema(description = "This is total sum of amount in INR")
    private Float totalINR;
    @Schema(description = "This is total sum of amount in USD")
    private Float totalUSD;
    @Schema(description = "Status of the HTTP request")
    private HttpStatus status;
    @Schema(description = "Contains message which states the status of request")
    private String message;
}
