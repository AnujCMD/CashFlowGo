package io.products.CashFlowGo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.products.CashFlowGo.model.request.Record;
import io.products.CashFlowGo.model.request.RecordFetchRequest;
import io.products.CashFlowGo.model.response.RecordResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Service interface for managing financial transactions.
 */
public interface TransactionService {

    /**
     * Registers a new transaction.
     *
     * @param record The transaction record to be registered.
     * @return A Mono wrapping the ResponseEntity with the status of the transaction registration.
     * @throws JsonProcessingException Thrown if there is an issue processing JSON data.
     */
    Mono<ResponseEntity<RecordResponse>>  registerTransaction(Record record) throws JsonProcessingException;

    /**
     * Fetches transaction details based on the provided RecordFetchRequest.
     *
     * @param recordFetchRequest The request object containing details for fetching transactions.
     * @return A Mono wrapping the ResponseEntity with the status and fetched transaction details.
     */
    Mono<ResponseEntity<RecordResponse>> getTransactionDetails(RecordFetchRequest recordFetchRequest);
}
