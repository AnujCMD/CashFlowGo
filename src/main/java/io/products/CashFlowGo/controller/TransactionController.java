package io.products.CashFlowGo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.products.CashFlowGo.model.request.Record;
import io.products.CashFlowGo.model.request.RecordFetchRequest;
import io.products.CashFlowGo.model.request.enums.TimeStampType;
import io.products.CashFlowGo.model.response.RecordResponse;
import io.products.CashFlowGo.service.TransactionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Controller class for managing transactions in CashFlowGo.
 *
 * This class handles the registration of transactions into CashFlowGo and provides
 * endpoints for fetching transactions with smart filters.
 */
@RestController
@CrossOrigin
@Tag(name = "CashFlowGo Transaction Gateway", description = "The gateway for Registering your transactions into CashFlowGo, and fetching it with smart filters")
public class TransactionController {
    TransactionService transactionService;
    RestTemplate restTemplate;

    @Autowired
    public TransactionController(TransactionService transactionService, RestTemplate restTemplate){
        this.transactionService = transactionService;
        this.restTemplate = restTemplate;
    }

    /**
     * Endpoint for registering a transaction into CashFlowGo.
     *
     * This method handles the creation of a new transaction by generating a unique
     * identifier, setting the current timestamp, and then invoking the transaction
     * service to register the transaction in the database.
     *
     * @param record The Record object containing details of the transaction.
     * @return A Mono wrapping the ResponseEntity with the status of the transaction registration.
     * @throws JsonProcessingException Thrown if there is an issue processing JSON data.
     */
    @PostMapping("/v1/create/transact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the transaction."),
            @ApiResponse(responseCode = "500", description = "Some Failure while registering the transaction"),
            @ApiResponse(responseCode = "400", description = "Bad Request,Invalid Request Details"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
    })
    @Tag(name = "Register Transaction", description = "Registers the transaction into Database")
    public Mono<ResponseEntity<RecordResponse>> registerTransaction(@RequestBody Record record) throws JsonProcessingException {

        // Set the uuid for the id of transaction.
        record.setId(UUID.randomUUID().toString());

        // Set the current timestamp into Record object.
        record.setTimestamp(LocalDateTime.now());

        // Call the service to register the transaction.
        return transactionService. registerTransaction(record);
    }

    /**
     * Endpoint for fetching transactions based on the given time in CashFlowGo.
     *
     * This method retrieves transaction details by invoking the transaction service
     * with the provided RecordFetchRequest, which includes the current timestamp.
     *
     * @param from The from contains the epoch in milliseconds for Custom time.
     * @param to The to contains the epoch in milliseconds for Custom time.
     * @param timeStampType The timeStampType contains quick filters to quickly access the transactions.
     * @return A Mono wrapping the ResponseEntity with the status and fetched transaction details.
     */
    @GetMapping("v1/transact")
    @Tag(name = "Fetch Transaction", description = "Fetches the transaction based on given time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the transaction."),
            @ApiResponse(responseCode = "500", description = "Some Failure while fetching the transaction"),
            @ApiResponse(responseCode = "400", description = "Bad Request,Invalid Request Details"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
    })
    public Mono<ResponseEntity<RecordResponse>> getTransactionsByTime(  @RequestParam(required = false) Long from,
                                                                        @RequestParam(required = false) Long to,
                                                                        @RequestParam TimeStampType timeStampType){
        RecordFetchRequest recordFetchRequest = new RecordFetchRequest();
        recordFetchRequest.setEpochFrom(from);
        recordFetchRequest.setEpochTo(to);
        recordFetchRequest.setTimeStampType(timeStampType);
        // Sets the current timestamp into RecordFetchRequest object.
        recordFetchRequest.setCurrentTimeStamp(LocalDateTime.now());

        // Call the service to fetch the transaction details.
        return transactionService.getTransactionDetails(recordFetchRequest);
    }
}
