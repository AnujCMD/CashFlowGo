package io.products.CashFlowGo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.products.CashFlowGo.model.request.Record;
import io.products.CashFlowGo.model.response.RecordResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<ResponseEntity<RecordResponse>>  registerTransaction(Record record) throws JsonProcessingException;
    Mono<ResponseEntity<RecordResponse>> getTransactionDetails();
}
